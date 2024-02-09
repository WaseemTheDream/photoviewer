package com.example.android.photoviewer.data.local

import com.example.android.photoviewer.data.model.Photo

interface PhotoLocalDataSource {

    fun putPhoto(photo: Photo)

    fun getPhoto(photoId: Int): Photo?
}