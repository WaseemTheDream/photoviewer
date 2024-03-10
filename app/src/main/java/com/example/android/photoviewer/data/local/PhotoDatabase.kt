package com.example.android.photoviewer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.photoviewer.data.entity.PhotoEntity

@Database(entities = [PhotoEntity::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao
}