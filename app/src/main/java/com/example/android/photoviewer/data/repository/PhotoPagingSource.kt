package com.example.android.photoviewer.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSource
import retrofit2.HttpException
import java.io.IOException

class PhotoPagingSource(
    private val remoteDataSource: PhotoRemoteDataSource,
) : PagingSource<Int, Photo>() {

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val currentPage = params.key ?: 1
            val photos = remoteDataSource.getPhotos(
                apiKey = Constants.PHOTOS_API_KEY,
                pageNumber = currentPage)
            val photosResponse = photos.body()
                ?: return LoadResult.Error(IllegalStateException("Photos response was empty."))

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