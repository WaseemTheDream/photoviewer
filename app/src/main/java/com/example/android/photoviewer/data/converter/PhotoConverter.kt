package com.example.android.photoviewer.data.converter

import com.example.android.photoviewer.data.entity.PhotoEntity
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.model.PhotoSource

fun Photo.toEntity(): PhotoEntity =
    PhotoEntity(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        photographer = this.photographer,
        description = this.description,
        srcOriginal = this.source.original)

fun PhotoEntity.toDomain(): Photo =
    Photo(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        source = PhotoSource(this.srcOriginal),
        photographer = this.photographer,
        description = this.description)