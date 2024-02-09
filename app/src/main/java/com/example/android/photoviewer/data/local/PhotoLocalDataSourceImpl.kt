package com.example.android.photoviewer.data.local

import com.example.android.photoviewer.data.model.Photo

class PhotoLocalDataSourceImpl : PhotoLocalDataSource {

    private val photoStore: MutableMap<Int, Photo> = mutableMapOf()
    override fun putPhoto(photo: Photo) {
        photoStore[photo.id] = photo
    }

    override fun getPhoto(photoId: Int): Photo? = photoStore[photoId]
}