package com.example.android.photoviewer.data.local

import androidx.paging.PagingSource
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
    suspend fun getPhoto(photoId: Int): Flow<Photo?>

    /**
     * Adds the specified photo into the local database.
     */
    suspend fun addPhoto(photo: Photo)


    /**
     * Deletes the specified photo from the local database.
     */
    suspend fun deletePhoto(photo: Photo)

    /**
     * Returns whether a photo with the specified id exists in the local database.
     * Does not check whether the photo is cached.
     */
    suspend fun existsInDatabase(photoId: Int): Flow<Boolean>
}