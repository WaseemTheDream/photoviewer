package com.example.android.photoviewer.core.di

import com.example.android.photoviewer.core.network.PhotoApi
import com.example.android.photoviewer.data.local.PhotoLocalDataSource
import com.example.android.photoviewer.data.local.PhotoLocalDataSourceImpl
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSource
import com.example.android.photoviewer.data.remote.PhotoRemoteDataSourceImpl
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
    fun providePhotoLocalDataSource(): PhotoLocalDataSource = PhotoLocalDataSourceImpl()

    @Singleton
    @Provides
    fun providePhotoRepository(
        photoRemoteDataSource: PhotoRemoteDataSource,
        photoLocalDataSource: PhotoLocalDataSource,
    ): PhotoRepository {
        return PhotoRepositoryImpl(photoRemoteDataSource, photoLocalDataSource)
    }
}