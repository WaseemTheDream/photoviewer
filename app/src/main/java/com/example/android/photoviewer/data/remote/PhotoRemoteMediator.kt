package com.example.android.photoviewer.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.data.converter.toEntity
import com.example.android.photoviewer.data.entity.PhotoEntity
import com.example.android.photoviewer.data.entity.PhotoRemoteKey
import com.example.android.photoviewer.data.local.PhotoDao
import com.example.android.photoviewer.data.local.PhotoDatabase
import com.example.android.photoviewer.data.local.PhotoRemoteKeyDao
import retrofit2.HttpException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator @Inject constructor(
    private val remoteDataSource: PhotoRemoteDataSource,
    private val photoDb: PhotoDatabase
): RemoteMediator<Int, PhotoEntity>() {

    private val dao: PhotoDao = photoDb.photoDao()
    private val remoteKeyDao: PhotoRemoteKeyDao = photoDb.photoRemoteKeyDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PhotoEntity>): MediatorResult {
        return try {
            loadHelper(loadType, state)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun loadHelper(loadType: LoadType, state: PagingState<Int, PhotoEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                1
            }
            LoadType.PREPEND -> {
                val remoteKey = getRemoteKeyForFirstItem(state)
                val prevPage = remoteKey?.prevPage
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null)
                prevPage
            }
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                val nextPage = remoteKey?.nextPage
                    ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKey != null)
                nextPage
            }
        }

        val response = remoteDataSource.getPhotos(
            apiKey = Constants.PHOTOS_API_KEY,
            pageNumber = page)
        if (!response.isSuccessful) {
            return MediatorResult.Error(HttpException(response))
        }

        var endOfPaginationReached = false

        val photosResponse = response.body()
            ?: return MediatorResult.Error(HttpException(response))

        photoDb.withTransaction {
            if (loadType == LoadType.REFRESH) {
                dao.deleteAll()
                remoteKeyDao.deleteAllPhotoRemoteKeys()
            }

            var prevPage: Int?
            var nextPage: Int

            photosResponse.page.let { pageNumber ->
                nextPage = pageNumber + 1
                prevPage = if (pageNumber <= 1) null else pageNumber - 1
            }

            val photoEntities = photosResponse.photos
                .mapIndexed { index, photo -> photo.toEntity(index) }

            val keys = photosResponse.photos.map { photo ->
                PhotoRemoteKey(
                    photoId = photo.id,
                    prevPage = prevPage,
                    nextPage = nextPage,
                    lastUpdated = System.currentTimeMillis()
                )
            }

            dao.addPhotos(photoEntities)
            remoteKeyDao.addAllPhotoRemoteKeys(keys)

            endOfPaginationReached = photosResponse.photos.isEmpty()
        }

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, PhotoEntity>
    ): PhotoRemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { photo ->
                remoteKeyDao.getPhotoRemoteKey(photoId = photo.primaryId)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, PhotoEntity>
    ): PhotoRemoteKey? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { photo ->
                remoteKeyDao.getPhotoRemoteKey(photoId = photo.primaryId)
            }
    }
}