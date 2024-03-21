package com.example.android.photoviewer.ui.photoslist

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.android.photoviewer.data.model.Photo

@Composable
fun ItemPhotoCell(photo: Photo, modifier: Modifier = Modifier) {
    AsyncImage(
        modifier = modifier.height(200.dp),
        model = photo.source.medium,
        contentDescription = photo.description,
        contentScale = ContentScale.Crop)
}