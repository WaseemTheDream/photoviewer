package com.example.android.photoviewer.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.photoviewer.core.app.Constants.PHOTOS_TABLE
import com.example.android.photoviewer.core.app.Constants.SAVED_PHOTOS_TABLE
import com.example.android.photoviewer.data.entity.PhotoEntity
import com.example.android.photoviewer.data.entity.SavedPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedPhotoDao {

    @Query("SELECT * FROM $SAVED_PHOTOS_TABLE ORDER BY last_updated_local DESC")
    fun getAllPhotos(): Flow<List<SavedPhotoEntity>>

    @Query("SELECT * FROM $SAVED_PHOTOS_TABLE ORDER BY last_updated_local DESC")
    fun getAllPhotosPagingSource(): PagingSource<Int, SavedPhotoEntity>

    @Query("SELECT EXISTS(SELECT * FROM $SAVED_PHOTOS_TABLE WHERE id=:photoId)")
    fun exists(photoId: Int): Flow<Boolean>

    @Query("SELECT * FROM $SAVED_PHOTOS_TABLE WHERE id=:photoId")
    fun getPhoto(photoId: Int): Flow<SavedPhotoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(photo: SavedPhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhotos(photos: List<SavedPhotoEntity>)

    @Delete
    suspend fun deletePhoto(photo: SavedPhotoEntity)

    @Query("DELETE FROM $SAVED_PHOTOS_TABLE")
    suspend fun deleteAll()
}