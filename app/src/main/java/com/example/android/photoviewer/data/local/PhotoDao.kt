package com.example.android.photoviewer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.photoviewer.core.app.Constants.PHOTOS_TABLE
import com.example.android.photoviewer.data.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {

    @Query("SELECT * FROM $PHOTOS_TABLE")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM $PHOTOS_TABLE WHERE id=:photoId")
    fun getPhoto(photoId: Int): Flow<PhotoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(photo: PhotoEntity)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("DELETE FROM $PHOTOS_TABLE")
    suspend fun deleteAll()
}