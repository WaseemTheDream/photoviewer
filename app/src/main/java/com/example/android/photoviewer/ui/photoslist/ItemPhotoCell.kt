package com.example.android.photoviewer.ui.photoslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.android.photoviewer.data.model.Photo

@Composable
fun ItemPhotoCell(photo: Photo, modifier: Modifier = Modifier, isSelected: Boolean) {
    val height =
        if (isSelected) {
            180.dp
        } else {
            200.dp
        }

    val shape =
        if (isSelected) {
            RoundedCornerShape(8.dp)
        } else {
            RectangleShape
        }

    AsyncImage(
        modifier = modifier
            .height(height)
            .clip(shape),
        model = photo.source.medium,
        contentDescription = photo.description,
        contentScale = ContentScale.Crop)
}