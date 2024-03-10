package com.example.android.photoviewer.data.local

import com.example.android.photoviewer.data.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoLocalDataSource {
    /**
     * Caches the photo in local memory. Does not save to a database.
     */
    fun cachePhoto(photo: Photo)

    /**
     * Fetches a photo from local memory (preferred) or database.
     */
    suspend fun getPhoto(photoId: Int): Flow<Photo>

    /**
     * Adds the specified photo into the local database.
     */
    suspend fun addPhoto(photo: Photo)
}