package com.example.android.photoviewer.data.repository

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSource
import retrofit2.HttpException
import java.io.IOException
import kotlin.random.Random

class PhotoPagingSource(
    private val remoteDataSource: PhotoRemoteDataSource,
    private val localDataSource: PhotoLocalDataSource,
) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val currentPage = params.key ?: 1
            val response = remoteDataSource.getPhotos(
                apiKey = Constants.PHOTOS_API_KEY,
                pageNumber = currentPage)

            if (!response.isSuccessful) {
                return when {
                    (response.code() == 401) -> LoadResult.Error(IllegalStateException("HTTP 401: Unauthorized"))
                    else -> LoadResult.Error(HttpException(response))
                }
            }

            if (Constants.DEBUG) {
                val random = Random.nextInt(0, 100)
                if (random >= 50) {
                    return LoadResult.Error(IllegalStateException("Random Error"))
                }
            }

            val photosResponse = response.body()
                ?: return LoadResult.Error(HttpException(response))

            photosResponse.photos.forEach { localDataSource.putPhoto(it) }

            LoadResult.Page(
                data = photosResponse.photos,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (photosResponse.photos.isEmpty()) null else photosResponse.page + 1
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }
}