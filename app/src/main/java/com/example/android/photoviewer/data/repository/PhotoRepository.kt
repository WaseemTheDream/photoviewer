package com.example.android.photoviewer.data.repository

import androidx.paging.PagingData
import com.example.android.photoviewer.data.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {

    suspend fun getPhotos(): Flow<PagingData<Photo>>
}