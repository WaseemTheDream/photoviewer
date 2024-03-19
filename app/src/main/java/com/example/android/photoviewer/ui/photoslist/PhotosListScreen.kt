package com.example.android.photoviewer.ui.photoslist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.android.photoviewer.R
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.ui.common.AppBarTitle
import com.example.android.photoviewer.ui.common.AppMenuButton
import com.example.android.photoviewer.ui.common.ErrorMessage
import com.example.android.photoviewer.ui.common.LoadingNextPageItem
import com.example.android.photoviewer.ui.common.PageLoader
import com.example.android.photoviewer.ui.common.ThemeSwitcher
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.model.DisplayStyle
import com.example.android.photoviewer.ui.model.PhotosDataSource


@Composable
fun PhotosListScreen(
    mainViewModel: MainViewModel,
    viewModel: PhotosListViewModel = hiltViewModel(),
    dataSource: PhotosDataSource,
    openNavigationDrawer: () -> Unit,
    navigateToDetailsScreen: (dataSource: PhotosDataSource, photoId: String) -> Unit
) {

    LaunchedEffect(Unit) {
        when (dataSource) {
            PhotosDataSource.HOME -> viewModel.getRemotePhotos()
            PhotosDataSource.SAVED -> viewModel.getSavedPhotos()
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppMenuButton(openNavigationDrawer)

                AppBarTitle(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.weight(1f))

                ThemeSwitcher(mainViewModel = mainViewModel)
                DisplayStyleSelector(viewModel) {
                    newStyle -> viewModel.updateDisplayStyle(newStyle)
                }
            }
        }
    ) {
        val selectedStyle by viewModel.displayStyleState.collectAsState()

        val clickListener: (Photo) -> Unit = { photo ->
            navigateToDetailsScreen(dataSource, photo.id.toString())
        }

        when (selectedStyle) {
            DisplayStyle.Card -> PhotosCardListScreenContent(it, viewModel, clickListener)
            DisplayStyle.Grid -> PhotosGridScreenContent(it, viewModel, clickListener)
        }
    }
}

@Composable
fun DisplayStyleSelector(
    viewModel: PhotosListViewModel,
    onStyleChanged: (DisplayStyle) -> Unit) {
    val selectedStyle by viewModel.displayStyleState.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    IconButton(onClick = { expanded = true }) { 
        Icon(
            painter = painterResource(id = R.drawable.ic_vertical_menu), 
            contentDescription = stringResource(id = R.string.select_display_style),
            tint = MaterialTheme.colorScheme.onPrimary)
        DropdownMenu(
            expanded = expanded, 
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.display_style_card)) }, 
                onClick = { onStyleChanged(DisplayStyle.Card) },
                trailingIcon = {  DisplayStyleDropdownMenuItemTrailingIcon(DisplayStyle.Card, selectedStyle)})
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.display_style_grid)) }, 
                onClick = { onStyleChanged(DisplayStyle.Grid) },
                trailingIcon = { DisplayStyleDropdownMenuItemTrailingIcon(DisplayStyle.Grid, selectedStyle) })
        }
    }
}

@Composable
fun DisplayStyleDropdownMenuItemTrailingIcon(styleItem: DisplayStyle, selectedStyle: DisplayStyle) {
    if (styleItem != selectedStyle) {
        return
    }
    Icon(
        painter = painterResource(id = R.drawable.baseline_check),
        contentDescription = null)
}

private const val COLUMN_COUNT = 3
private val span: (LazyGridItemSpanScope) -> GridItemSpan = { GridItemSpan(COLUMN_COUNT) }

@Composable
fun PhotosGridScreenContent(
    paddingValues: PaddingValues,
    viewModel: PhotosListViewModel,
    photoClickListener: (Photo) -> Unit) {
    val photoPagingItems: LazyPagingItems<Photo> = viewModel.photosState.collectAsLazyPagingItems()

    if (photoPagingItems.loadState.refresh is LoadState.Loading) {
        PageLoader(modifier = Modifier.fillMaxSize())
    }

    if (photoPagingItems.loadState.refresh is LoadState.Error) {
        val error = photoPagingItems.loadState.refresh as LoadState.Error
        val errorMessage =
            error.error.localizedMessage ?:
            stringResource(id = R.string.unknown_error)
        if (photoPagingItems.itemCount > 0) {
            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG).show()
        } else {
            ErrorMessage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                message = errorMessage
            ) { photoPagingItems.retry() }
        }
    }

    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        columns = GridCells.Fixed(COLUMN_COUNT),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(photoPagingItems.itemCount) {index ->
            photoPagingItems.get(index)?.let {
                ItemPhotoCell(photo = it, photoClickListener)
            }
        }

        photoPagingItems.apply {
            when {
                loadState.append is LoadState.Loading -> {
                    item(span = span) { LoadingNextPageItem() }
                }

                loadState.append is LoadState.Error -> {
                    val error = photoPagingItems.loadState.append as LoadState.Error
                    item(span = span) {
                        val errorMessage =
                            error.error.localizedMessage ?:
                            stringResource(id = R.string.unknown_error)
                        ErrorMessage(
                            modifier = Modifier.padding(20.dp),
                            message = errorMessage
                        ) { retry() }
                    }
                }
            }
        }
    }
}

@Composable
fun PhotosCardListScreenContent(
    paddingValues: PaddingValues,
    viewModel: PhotosListViewModel,
    photoClickListener: (Photo) -> Unit) {
    val photoPagingItems: LazyPagingItems<Photo> = viewModel.photosState.collectAsLazyPagingItems()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { Spacer(modifier = Modifier.padding(2.dp)) }
        items(photoPagingItems.itemCount) { index ->
            photoPagingItems.get(index)?.let {
                ItemPhotoCard(index = index, photo = it, photoClickListener)
            }
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

                        if (photoPagingItems.itemCount > 0) {
                            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_LONG).show()
                        } else {
                            ErrorMessage(
                                modifier = Modifier
                                    .fillParentMaxSize()
                                    .padding(20.dp),
                                message = errorMessage
                            ) { retry() }
                        }
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
                            message = errorMessage
                        ) { retry() }
                    }
                }
            }
        }
    }
}