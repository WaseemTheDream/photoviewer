package com.example.android.photoviewer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val remoteDataSource: PhotoRemoteDataSource
) : PhotoRepository {

    override suspend fun getPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = Constants.MAX_PAGE_SIZE, prefetchDistance = 2),
            pagingSourceFactory = {
                PhotoPagingSource(remoteDataSource)
            }
        ).flow
    }
}