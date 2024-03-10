package com.example.android.photoviewer.core.di

import android.content.Context
import androidx.room.Room
import com.example.android.photoviewer.core.app.Constants.PHOTOS_DATABASE
import com.example.android.photoviewer.data.local.PhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        PhotoDatabase::class.java,
        PHOTOS_DATABASE).build()

    @Singleton
    @Provides
    fun provideDao(database: PhotoDatabase) = database.photoDao()
}