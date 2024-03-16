package com.example.android.photoviewer.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.photoviewer.core.app.Constants.PHOTOS_REMOTE_KEYS_TABLE

@Entity(tableName = PHOTOS_REMOTE_KEYS_TABLE)
data class PhotoRemoteKey(
    @PrimaryKey(autoGenerate = false)
    val photoId: Int,
    val prevPage: Int?,
    val nextPage: Int?,
    val lastUpdated: Long?,
)
