package com.example.android.photoviewer.ui.photoslist

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.android.photoviewer.data.model.Photo

@Composable
fun ItemPhoto(index: Int, photo: Photo) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(0.9f),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        AsyncImage(
            modifier = Modifier.height(400.dp),
            model = photo.source.original,
            contentDescription = photo.description,
            contentScale = ContentScale.Crop)

        Text(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    appendLine((index + 1).toString())
                    append("Photographer: ")
                }
                append(photo.photographer)
            },
            fontSize = 12.sp)

        if (!photo.description.isNullOrBlank()) {
            Text(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                text = photo.description,
                fontSize = 12.sp)
        }
    }
}