package com.example.android.photoviewer.data.local

import com.example.android.photoviewer.data.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoLocalDataSource {
    /**
     * Fetches a photo from the cached photos table.
     */
    suspend fun getPhoto(photoId: Int): Flow<Photo?>

    /**
     * Fetches a photo from the saved photos table.
     */
    suspend fun getSavedPhoto(photoId: Int): Flow<Photo?>

    /**
     * Adds the specified photo into the saved photos table.
     */
    suspend fun savePhoto(photo: Photo)


    /**
     * Deletes the specified photo from the saved photos table.
     */
    suspend fun unSavePhoto(photo: Photo)

    /**
     * Returns whether a photo with the specified id exists in the saved table.
     */
    suspend fun isSaved(photoId: Int): Flow<Boolean>
}