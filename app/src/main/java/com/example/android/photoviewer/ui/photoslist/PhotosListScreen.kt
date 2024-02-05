package com.example.android.photoviewer.ui.photoslist

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
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
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosListScreen(
    mainViewModel: MainViewModel,
    viewModel: PhotosListViewModel = hiltViewModel()
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
        PhotosListScreenContent(it, viewModel)
    }
}

@Composable
fun ThemeSwitcher(mainViewModel: MainViewModel) {
    val appTheme by mainViewModel.appTheme.collectAsState()
    IconButton(onClick = {
        if (appTheme == AppTheme.Light) {
            mainViewModel.updateAppTheme(AppTheme.Dark)
        } else {
            mainViewModel.updateAppTheme(AppTheme.Light)
        }
    }) {
        Icon(
            painter = if (appTheme == AppTheme.Light)
                painterResource(id = R.drawable.ic_dark_mode)
            else
                painterResource(id = R.drawable.ic_light_mode),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(25.dp)
        )
    }
}

@Composable
fun PhotosListScreenContent(paddingValues: PaddingValues, viewModel: PhotosListViewModel) {
    val photoPagingItems: LazyPagingItems<Photo> = viewModel.photosState.collectAsLazyPagingItems()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
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
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(20.dp),
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