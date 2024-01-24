package com.example.android.photoviewer.data.remote

import retrofit2.Response

interface PhotoRemoteDataSource {

    suspend fun getPhotos(
        apiKey: String,
        pageNumber: Int,
        perPage: Int = 20
    ): Response<PhotoResponse>
}