package com.example.android.photoviewer.data.remote

import com.example.android.photoviewer.core.app.Constants
import retrofit2.Response

interface PhotoRemoteDataSource {

    suspend fun getPhotos(
        apiKey: String,
        pageNumber: Int,
        perPage: Int = Constants.MAX_PAGE_SIZE
    ): Response<PhotoResponse>
}