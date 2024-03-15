package com.example.android.photoviewer.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.photoviewer.core.app.Constants


@Entity(tableName = Constants.PHOTOS_TABLE)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo("width")
    val width: Int,

    @ColumnInfo("height")
    val height: Int,

    @ColumnInfo("url")
    val url: String,

    @ColumnInfo("photographer")
    val photographer: String,

    @ColumnInfo("description")
    val description: String,

    @ColumnInfo("src_original")
    val srcOriginal: String,

    @ColumnInfo("src_medium")
    val srcMedium: String
)
