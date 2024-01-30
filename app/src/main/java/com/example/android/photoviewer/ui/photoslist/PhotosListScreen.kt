package com.example.android.photoviewer.ui.photoslist

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.android.photoviewer.data.model.Photo


@Composable
fun PhotosListScreen(
    viewModel: PhotosListViewModel = hiltViewModel()
) {
    val TAG = "PhotosListScreen"
    val photoPagingItems: LazyPagingItems<Photo> = viewModel.photosState.collectAsLazyPagingItems()
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        items(photoPagingItems.itemCount) { index ->
            Log.d(TAG, "Photo Item: $index")
            ItemPhoto(index = index, photo = photoPagingItems[index]!!)
        }
    }
}