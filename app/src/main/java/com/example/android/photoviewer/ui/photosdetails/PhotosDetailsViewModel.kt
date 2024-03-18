package com.example.android.photoviewer.ui.photosdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun getPhoto(photoId: Int) {
        viewModelScope.launch {
            photoDataSource
                .getPhoto(photoId)
                .collect {
                    _photo.value = it
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
        }
    }

    fun unSavePhoto(photo: Photo) {
        viewModelScope.launch {
            photoDataSource.unSavePhoto(photo)
        }
    }
}