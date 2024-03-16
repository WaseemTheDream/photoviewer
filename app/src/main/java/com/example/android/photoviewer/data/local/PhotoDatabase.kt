package com.example.android.photoviewer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.photoviewer.data.entity.PhotoEntity
import com.example.android.photoviewer.data.entity.PhotoRemoteKey

@Database(entities = [PhotoEntity::class, PhotoRemoteKey::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    abstract fun photoRemoteKeyDao(): PhotoRemoteKeyDao
}