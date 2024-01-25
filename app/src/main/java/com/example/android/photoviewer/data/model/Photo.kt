package com.example.android.photoviewer.data.model

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("width")
    val width: Int,

    @SerializedName("height")
    val height: Int,

    @SerializedName("url")
    val url: String,

    @SerializedName("src")
    val source: PhotoSource,

    @SerializedName("photographer")
    val photographer: String,

    @SerializedName("alt")
    val description: String)

