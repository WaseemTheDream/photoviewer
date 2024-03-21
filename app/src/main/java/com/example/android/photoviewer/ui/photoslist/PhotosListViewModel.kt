package com.example.android.photoviewer.ui.photoslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.map
import com.example.android.photoviewer.data.converter.toDomain
import com.example.android.photoviewer.data.entity.SavedPhotoEntity
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.repository.AppSettingsRepository
import com.example.android.photoviewer.data.repository.PhotoRepository
import com.example.android.photoviewer.ui.model.DisplayStyle
import com.example.android.photoviewer.ui.model.PhotoSelectionStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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

    init {
        readDisplayStyle()
        observeSelectedPhotos()
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
            localPhotosDataSource.savePhotos(selectedPhotos.value)
        }
    }

    fun unSaveSelectedPhotos() {
        viewModelScope.launch {
            localPhotosDataSource.unSavePhotos(selectedPhotos.value)
        }
    }

    fun clearSelection() {
        _selectedPhotos.value = listOf()
    }

    private fun observeSelectedPhotos() {
        viewModelScope.launch {
            selectedPhotos.collect {
                val savedPhotos = it.fold(0) { acc, photo ->
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
                    it.size -> PhotoSelectionStatus.ALL_SAVED
                    else -> PhotoSelectionStatus.SOME_SAVED
                }
                _selectedPhotosStatus.value = selectionStatus
            }
        }
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