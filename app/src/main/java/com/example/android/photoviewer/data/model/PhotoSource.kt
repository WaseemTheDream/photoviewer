package com.example.android.photoviewer.data.model

import com.google.gson.annotations.SerializedName

data class PhotoSource(
    @SerializedName("original")
    val original: String)
