package com.example.android.photoviewer.data.remote

import com.example.android.photoviewer.core.network.PhotoApi
import kotlinx.coroutines.delay
import retrofit2.Response
import javax.inject.Inject

class PhotoRemoteDataSourceImpl @Inject constructor(
    private val remoteApi: PhotoApi
) : PhotoRemoteDataSource {

    override suspend fun getPhotos(
        apiKey: String,
        pageNumber: Int,
        perPage: Int
    ): Response<PhotoResponse> {
        // Add 5 second artificial delay to verify loading indicator
        delay(5000)
        return remoteApi.getPhotos(
            token =  apiKey,
            pageNumber = pageNumber,
            perPage = perPage)
    }
}