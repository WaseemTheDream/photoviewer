package com.example.android.photoviewer.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.photoviewer.core.app.Constants.PHOTOS_REMOTE_KEYS_TABLE
import com.example.android.photoviewer.data.entity.PhotoRemoteKey


@Dao
interface PhotoRemoteKeyDao {

    @Query("SELECT * FROM $PHOTOS_REMOTE_KEYS_TABLE WHERE photoId=:photoId")
    suspend fun getPhotoRemoteKey(photoId: Int): PhotoRemoteKey?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAllPhotoRemoteKeys(remoteKeys: List<PhotoRemoteKey>)

    @Query("DELETE FROM $PHOTOS_REMOTE_KEYS_TABLE")
    suspend fun deleteAllPhotoRemoteKeys()
}