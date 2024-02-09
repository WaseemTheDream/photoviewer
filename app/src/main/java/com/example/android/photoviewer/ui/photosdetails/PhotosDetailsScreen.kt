package com.example.android.photoviewer.ui.photosdetails

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android.photoviewer.R
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.photoslist.ThemeSwitcher

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosDetailsScreen(
    mainViewModel: MainViewModel,
    photoId: Int?,
    viewModel: PhotosDetailsViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        Icons.Default.Menu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Text(
                    text = stringResource(id = R.string.app_name),
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 16.dp)
                        .weight(1.0f),
                    textAlign = TextAlign.Center)

                ThemeSwitcher(mainViewModel = mainViewModel)
            }
        }
    ) {
        PhotosDetailsScreenContent(photoId = photoId, viewModel = viewModel, paddingValues = it)
    }
}

@Composable
fun PhotosDetailsScreenContent(
    photoId: Int?,
    viewModel: PhotosDetailsViewModel,
    paddingValues: PaddingValues,
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        if (photoId == null) {
            Text(text = "Photo Id not specified")
            return
        }
        val photo = viewModel.getPhoto(photoId)
        if (photo == null) {
            Text("Photo not found")
            return
        }
        Text(text = "Photo found: ${photo.id} ${photo.url}")
    }
}