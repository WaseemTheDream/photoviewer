package com.example.android.photoviewer.ui.photoslist

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.android.photoviewer.R
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.ui.common.ErrorMessage
import com.example.android.photoviewer.ui.common.LoadingNextPageItem
import com.example.android.photoviewer.ui.common.PageLoader


@Composable
fun PhotosListScreen(
    viewModel: PhotosListViewModel = hiltViewModel()
) {
    val photoPagingItems: LazyPagingItems<Photo> = viewModel.photosState.collectAsLazyPagingItems()
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Spacer(modifier = Modifier.padding(2.dp)) }
        items(photoPagingItems.itemCount) { index ->
            ItemPhoto(index = index, photo = photoPagingItems[index]!!)
        }
        photoPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { PageLoader(modifier = Modifier.fillParentMaxSize()) }
                }

                loadState.refresh is LoadState.Error -> {
                    val error = photoPagingItems.loadState.refresh as LoadState.Error
                    item {
                        val errorMessage =
                            error.error.localizedMessage ?:
                            stringResource(id = R.string.unknown_error)
                        ErrorMessage(
                            modifier = Modifier.fillParentMaxSize().padding(20.dp),
                            message = errorMessage,
                            onClickRetry = { retry() })
                    }
                }

                loadState.append is LoadState.Loading -> {
                    item { LoadingNextPageItem() }
                }

                loadState.append is LoadState.Error -> {
                    val error = photoPagingItems.loadState.append as LoadState.Error
                    item {
                        val errorMessage =
                            error.error.localizedMessage ?:
                            stringResource(id = R.string.unknown_error)
                        ErrorMessage(
                            modifier = Modifier.padding(20.dp),
                            message = errorMessage,
                            onClickRetry = { retry() })
                    }
                }
            }
        }
    }
}