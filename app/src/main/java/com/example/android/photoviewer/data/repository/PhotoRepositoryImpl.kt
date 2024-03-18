package com.example.android.photoviewer.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.data.converter.toDomain
import com.example.android.photoviewer.data.local.PhotoDatabase
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSource
import com.example.android.photoviewer.data.remote.PhotoRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoRemoteMediator: PhotoRemoteMediator,
    private val photoDb: PhotoDatabase,
) : PhotoRepository {

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.MAX_PAGE_SIZE, prefetchDistance = 2),
            remoteMediator = photoRemoteMediator,
            pagingSourceFactory = {
                photoDb.photoDao().getAllPhotosPagingSource()
            }
        ).flow.map { pagingData ->
            pagingData.map { photoEntity -> photoEntity.toDomain() }
        }
    }
}