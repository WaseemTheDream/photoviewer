package com.example.android.photoviewer.data.remote

import com.example.android.photoviewer.data.model.Photo
import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("photos")
    val photos: List<Photo>
)
