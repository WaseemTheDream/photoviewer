package com.example.android.photoviewer.ui.photoslist

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.android.photoviewer.R
import com.example.android.photoviewer.data.converter.toDomain
import com.example.android.photoviewer.data.entity.SavedPhotoEntity
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.repository.AppSettingsRepository
import com.example.android.photoviewer.data.repository.PhotoRepository
import com.example.android.photoviewer.ui.model.DisplayStyle
import com.example.android.photoviewer.ui.model.PhotoSelectionStatus
import com.example.android.photoviewer.ui.model.SnackbarEvent
import com.example.android.photoviewer.ui.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PhotosListViewModel @Inject constructor(
    private val appSettingsRepository: AppSettingsRepository,
    private val photoRepository: PhotoRepository,
    private val localPhotos: Pager<Int, SavedPhotoEntity>,
    private val localPhotosDataSource: PhotoLocalDataSource
) : ViewModel() {

    private val _photosState: MutableStateFlow<PagingData<Photo>> =
        MutableStateFlow(value = PagingData.empty())
    val photosState: StateFlow<PagingData<Photo>> get() = _photosState

    private val _displayStyleState: MutableStateFlow<DisplayStyle> =
        MutableStateFlow(DisplayStyle.Card)
    val displayStyleState: StateFlow<DisplayStyle> = _displayStyleState

    private var isCollectingData: Boolean = false

    private val _selectedPhotosStatus: MutableStateFlow<PhotoSelectionStatus> =
        MutableStateFlow(PhotoSelectionStatus.NONE)
    val selectedPhotosStatus: StateFlow<PhotoSelectionStatus> = _selectedPhotosStatus

    private val _selectedPhotos: MutableStateFlow<List<Photo>> = MutableStateFlow(emptyList())
    val selectedPhotos: StateFlow<List<Photo>> = _selectedPhotos

    private val snackbarEventsChannel = Channel<SnackbarEvent>()
    val snackbarEvents: Flow<SnackbarEvent> = snackbarEventsChannel.receiveAsFlow()

    init {
        readDisplayStyle()
        observeSelectedPhotos()
        pruneSelectedPhotos()
    }

    fun getRemotePhotos() {
        if (isCollectingData) {
            return
        }

        viewModelScope.launch {
            isCollectingData = true
            photoRepository.getPhotos()
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .collect {
                    _photosState.value = it
                }
        }
    }

    fun getSavedPhotos() {
        if (isCollectingData) {
            return
        }

        viewModelScope.launch {
            isCollectingData = true

            localPhotos.flow
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .map { pagingData ->
                    pagingData.map { it.toDomain() }
                }
                .collect {
                    _photosState.value = it
            }
        }
    }

    fun selectPhoto(photo: Photo) {
        _selectedPhotos.value = selectedPhotos.value.plus(photo)
    }

    fun unselectPhoto(photo: Photo) {
        _selectedPhotos.value = selectedPhotos.value.minus(photo)
    }

    fun saveSelectedPhotos() {
        viewModelScope.launch {
            val photos = selectedPhotos.value
            localPhotosDataSource.savePhotos(photos)
            calculateSelectedPhotosStatus(photos)
            val count = photos.size
            snackbarEventsChannel.send(
                SnackbarEvent(
                    message = UiText.PluralStringResource(
                        R.plurals.num_photos_saved,
                        count,
                        count),
                    actionLabel = UiText.StringResource(R.string.undo),
                    duration = SnackbarDuration.Long,
                    onAction = {
                        viewModelScope.launch {
                            localPhotosDataSource.unSavePhotos(photos)
                        }
                    })
            )
        }
    }

    fun unSaveSelectedPhotos() {
        viewModelScope.launch {
            val photos = selectedPhotos.value
            localPhotosDataSource.unSavePhotos(photos)
            calculateSelectedPhotosStatus(photos)
            val count = photos.size
            snackbarEventsChannel.send(
                SnackbarEvent(
                    message = UiText.PluralStringResource(
                        R.plurals.num_photos_unsaved,
                        count,
                        count),
                    actionLabel = UiText.StringResource(R.string.undo),
                    duration = SnackbarDuration.Long,
                    onAction = {
                        viewModelScope.launch {
                            localPhotosDataSource.savePhotos(photos)
                        }
                    })
            )
        }
    }

    fun clearSelection() {
        _selectedPhotos.value = listOf()
    }

    private fun pruneSelectedPhotos() {
        viewModelScope.launch {
            photosState.collect { pagingData ->
                val collectionIds = mutableSetOf<Int>()
                pagingData.map { collectionIds.add(it.id) }
                _selectedPhotos.value =
                    _selectedPhotos.value.filter { collectionIds.contains(it.id) }
            }
        }
    }

    private fun observeSelectedPhotos() {
        viewModelScope.launch {
            selectedPhotos.collect {
                calculateSelectedPhotosStatus(it)
            }
        }
    }

    private suspend fun calculateSelectedPhotosStatus(photos: List<Photo>) {
        val savedPhotos = photos.fold(0) { acc, photo ->
            when (localPhotosDataSource.isSaved(photo.id).first()) {
                false -> acc
                true -> acc + 1
            }
        }
        val selectionStatus = when (savedPhotos) {
            0 ->
                if (selectedPhotos.value.isEmpty()) {
                    PhotoSelectionStatus.NONE
                } else {
                    PhotoSelectionStatus.NONE_SAVED
                }
            photos.size -> PhotoSelectionStatus.ALL_SAVED
            else -> PhotoSelectionStatus.SOME_SAVED
        }
        _selectedPhotosStatus.value = selectionStatus
    }

    private fun readDisplayStyle() {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsRepository.displayStyle
                .distinctUntilChanged().collect {
                _displayStyleState.value = it
            }
        }
    }

    fun updateDisplayStyle(displayStyle: DisplayStyle) {
        viewModelScope.launch(Dispatchers.IO) {
            appSettingsRepository.setDisplayStyle(displayStyle)
        }
    }
}