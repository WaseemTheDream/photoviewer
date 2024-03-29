package com.example.android.photoviewer.data.local

import androidx.paging.PagingSource
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

    @Query("SELECT * FROM $PHOTOS_TABLE ORDER BY last_updated_local ASC, secondary_id ASC")
    fun getAllPhotos(): Flow<List<PhotoEntity>>

    @Query("SELECT * FROM $PHOTOS_TABLE ORDER BY last_updated_local ASC, secondary_id ASC")
    fun getAllPhotosPagingSource(): PagingSource<Int, PhotoEntity>

    @Query("SELECT last_updated_local FROM $PHOTOS_TABLE ORDER BY last_updated_local ASC")
    fun firstUpdated(): Flow<Long?>

    @Query("SELECT EXISTS(SELECT * FROM $PHOTOS_TABLE WHERE id=:photoId)")
    fun exists(photoId: Int): Flow<Boolean>

    @Query("SELECT * FROM $PHOTOS_TABLE WHERE id=:photoId")
    fun getPhoto(photoId: Int): Flow<PhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhotos(photos: List<PhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("DELETE FROM $PHOTOS_TABLE")
    suspend fun deleteAll()
}