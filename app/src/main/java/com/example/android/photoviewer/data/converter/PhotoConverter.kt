package com.example.android.photoviewer.data.converter

import com.example.android.photoviewer.data.entity.PhotoEntity
import com.example.android.photoviewer.data.entity.SavedPhotoEntity
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.data.model.PhotoSource

fun Photo.toEntity(secondaryId: Int = 0): PhotoEntity =
    PhotoEntity(
        primaryId = this.id,
        secondaryId = secondaryId,
        width = this.width,
        height = this.height,
        url = this.url,
        photographer = this.photographer,
        description = this.description,
        srcOriginal = this.source.original,
        srcMedium = this.source.medium)

fun Photo.toSavedEntity(): SavedPhotoEntity =
    SavedPhotoEntity(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        photographer = this.photographer,
        description = this.description,
        srcOriginal = this.source.original,
        srcMedium = this.source.medium)

fun PhotoEntity.toDomain(): Photo =
    Photo(
        id = this.primaryId,
        width = this.width,
        height = this.height,
        url = this.url,
        source = PhotoSource(this.srcOriginal, this.srcMedium),
        photographer = this.photographer,
        description = this.description)

fun SavedPhotoEntity.toDomain(): Photo =
    Photo(
        id = this.id,
        width = this.width,
        height = this.height,
        url = this.url,
        source = PhotoSource(this.srcOriginal, this.srcMedium),
        photographer = this.photographer,
        description = this.description)
