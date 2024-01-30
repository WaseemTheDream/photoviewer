package com.example.android.photoviewer.core.network

import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.data.remote.PhotoResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PhotoApi {

    companion object {
        const val SERVER_URL = "https://api.pexels.com/"
        const val API_URL = "v1/curated/"
    }

    @GET(API_URL)
    suspend fun getPhotos(
        @Header("Authorization") token: String,
        @Query("page") pageNumber: Int,
        @Query("per_page") perPage: Int = Constants.MAX_PAGE_SIZE,
    ): Response<PhotoResponse>
}