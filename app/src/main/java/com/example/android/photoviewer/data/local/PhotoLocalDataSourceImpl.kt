package com.example.android.photoviewer.data.local

import com.example.android.photoviewer.data.converter.toDomain
import com.example.android.photoviewer.data.converter.toSavedEntity
import com.example.android.photoviewer.data.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoLocalDataSourceImpl @Inject constructor(
    private val photoDao: PhotoDao,
    private val savedPhotoDao: SavedPhotoDao
) : PhotoLocalDataSource {

    override suspend fun getPhoto(photoId: Int): Flow<Photo?> =
        photoDao.getPhoto(photoId).map { it?.toDomain() }

    override suspend fun getSavedPhoto(photoId: Int): Flow<Photo?> =
        savedPhotoDao.getPhoto(photoId).map { it?.toDomain() }

    override suspend fun savePhoto(photo: Photo) {
        savedPhotoDao.addPhoto(photo.toSavedEntity())
    }

    override suspend fun unSavePhoto(photo: Photo) {
        savedPhotoDao.deletePhoto(photo.toSavedEntity())
    }

    override suspend fun isSaved(photoId: Int): Flow<Boolean> =
        savedPhotoDao.exists(photoId)
}