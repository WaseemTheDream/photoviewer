package com.example.android.photoviewer.data.local

import com.example.android.photoviewer.data.converter.toDomain
import com.example.android.photoviewer.data.converter.toEntity
import com.example.android.photoviewer.data.model.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoLocalDataSourceImpl @Inject constructor(
    private val photoDao: PhotoDao
) : PhotoLocalDataSource {

    private val photoStore: MutableMap<Int, Photo> = mutableMapOf()
    override fun cachePhoto(photo: Photo) {
        photoStore[photo.id] = photo
    }

    override suspend fun getPhoto(photoId: Int): Flow<Photo> {
        if (photoStore.containsKey(photoId)) {
            return flow { emit(photoStore[photoId]!!) }
        }

        return photoDao.getPhoto(photoId).map { it.toDomain() }
    }

    override suspend fun addPhoto(photo: Photo) {
        photoDao.addPhoto(photo.toEntity())
    }

    override suspend fun deletePhoto(photo: Photo) {
        photoDao.deletePhoto(photo.toEntity())
    }

    override suspend fun existsInDatabase(photoId: Int): Flow<Boolean> =
        photoDao.exists(photoId)
}