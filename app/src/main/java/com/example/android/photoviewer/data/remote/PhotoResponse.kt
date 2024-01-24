package com.example.android.photoviewer.data.remote

import com.google.gson.annotations.SerializedName

data class PhotoResponse(
    @SerializedName("page")
    val page: Int,

    @SerializedName("per_page")
    val perPage: Int
)
