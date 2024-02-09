package com.example.android.photoviewer.ui.photosdetails

import androidx.lifecycle.ViewModel
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PhotosDetailsViewModel @Inject constructor(
    private val photoDataSource: PhotoLocalDataSource
) : ViewModel() {

    fun getPhoto(photoId: Int): Photo? {
        return photoDataSource.getPhoto(photoId)
    }
}