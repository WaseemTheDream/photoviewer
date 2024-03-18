package com.example.android.photoviewer.core.di

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.android.photoviewer.core.app.Constants
import com.example.android.photoviewer.core.network.PhotoApi
import com.example.android.photoviewer.data.entity.PhotoEntity
import com.example.android.photoviewer.data.entity.SavedPhotoEntity
import com.example.android.photoviewer.data.local.PhotoDao
import com.example.android.photoviewer.data.local.PhotoDatabase
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.local.PhotoLocalDataSourceImpl
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSource
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSourceImpl
import com.example.android.photoviewer.data.remote.PhotoRemoteMediator
import com.example.android.photoviewer.data.repository.PhotoRepository
import com.example.android.photoviewer.data.repository.PhotoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePhotoRemoteDataSource(
        remoteApi: PhotoApi
    ): PhotoRemoteDataSource = PhotoRemoteDataSourceImpl(remoteApi)

    @Singleton
    @Provides
    fun providePhotoLocalDataSource(
        photoDb: PhotoDatabase
    ): PhotoLocalDataSource = PhotoLocalDataSourceImpl(
        photoDb.photoDao(), photoDb.savedPhotoDao())

    @Singleton
    @Provides
    fun providePhotoRepository(
        photoRemoteMediator: PhotoRemoteMediator,
        photoDb: PhotoDatabase,
    ): PhotoRepository {
        return PhotoRepositoryImpl(photoRemoteMediator, photoDb)
    }

    @Singleton
    @Provides
    fun provideSavedPhotosPager(photoDatabase: PhotoDatabase): Pager<Int, SavedPhotoEntity> {
        return Pager(
            config = PagingConfig(pageSize = Constants.MAX_PAGE_SIZE),
            pagingSourceFactory = {
                photoDatabase.savedPhotoDao().getAllPhotosPagingSource()
            }
        )
    }
}