package com.example.android.photoviewer.ui.photosdetails

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.photoviewer.R
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.ui.model.PhotosDataSource
import com.example.android.photoviewer.ui.model.SnackbarEvent
import com.example.android.photoviewer.ui.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotosDetailsViewModel @Inject constructor(
    private val photoDataSource: PhotoLocalDataSource
) : ViewModel() {

    private val _photo: MutableStateFlow<Photo?> = MutableStateFlow(null)
    val photo: StateFlow<Photo?> = _photo

    private val _isSaved: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    val snackbarEventsChannel = Channel<SnackbarEvent>()
    val snackbarEvents: Flow<SnackbarEvent> = snackbarEventsChannel.receiveAsFlow()

    fun getPhoto(dataSource: PhotosDataSource, photoId: Int) {
        when (dataSource) {
            PhotosDataSource.HOME -> {
                viewModelScope.launch {
                    photoDataSource
                        .getPhoto(photoId)
                        .filterNotNull()
                        .collect {
                            _photo.value = it
                        }
                }
            }
            PhotosDataSource.SAVED -> {
                viewModelScope.launch {
                    photoDataSource
                        .getSavedPhoto(photoId)
                        .filterNotNull()
                        .collect {
                            _photo.value = it
                        }
                }
            }
        }

        viewModelScope.launch {
            photoDataSource
                .isSaved(photoId)
                .collect {
                    _isSaved.value = it
                }
        }
    }

    fun savePhoto(photo: Photo) {
        viewModelScope.launch {
            photoDataSource.savePhoto(photo)
            snackbarEventsChannel.send(
                SnackbarEvent(
                    message = UiText.PluralStringResource(
                        R.plurals.num_photos_saved,
                        1,
                        1),
                    actionLabel = UiText.StringResource(R.string.undo),
                    duration = SnackbarDuration.Long,
                    onAction = {
                        viewModelScope.launch {
                            photoDataSource.unSavePhoto(photo)
                        }
                    }))
        }
    }

    fun unSavePhoto(photo: Photo) {
        viewModelScope.launch {
            photoDataSource.unSavePhoto(photo)
            snackbarEventsChannel.send(
                SnackbarEvent(
                    message = UiText.PluralStringResource(
                        R.plurals.num_photos_unsaved,
                        1,
                        1),
                    actionLabel = UiText.StringResource(R.string.undo),
                    duration = SnackbarDuration.Long,
                    onAction = {
                        viewModelScope.launch {
                            photoDataSource.savePhoto(photo)
                        }
                    }))
        }
    }
}