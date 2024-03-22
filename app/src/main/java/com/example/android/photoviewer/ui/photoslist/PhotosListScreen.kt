package com.example.android.photoviewer.ui.photoslist

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.android.photoviewer.R
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.ui.common.TitleBarText
import com.example.android.photoviewer.ui.common.AppMenuButton
import com.example.android.photoviewer.ui.common.ErrorMessage
import com.example.android.photoviewer.ui.common.LoadingNextPageItem
import com.example.android.photoviewer.ui.common.PageLoader
import com.example.android.photoviewer.ui.common.ThemeSwitcher
import com.example.android.photoviewer.ui.common.TitleBarButton
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.model.DisplayStyle
import com.example.android.photoviewer.ui.model.PhotoSelectionStatus
import com.example.android.photoviewer.ui.model.PhotosDataSource
import com.example.android.photoviewer.ui.theme.AppTheme


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

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.snackbarEvents.collect {
            val result = snackbarHostState.showSnackbar(
                it.message.asString(context),
                it.actionLabel?.asString(context),
                duration = it.duration ?: SnackbarDuration.Short)
            when (result) {
                SnackbarResult.ActionPerformed -> it.onAction?.let { action -> action() }
                SnackbarResult.Dismissed -> {}
            }
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
                val selectionStatus = viewModel.selectedPhotosStatus.collectAsState()
                
                when (selectionStatus.value) {
                    PhotoSelectionStatus.NONE -> 
                        DefaultTopBar(mainViewModel, viewModel, openNavigationDrawer)
                    else ->
                        SelectionTopBar(viewModel)
                }
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) {
        val selectedStyle by viewModel.displayStyleState.collectAsState()

        val clickListener: (Photo) -> Unit = { photo ->
            navigateToDetailsScreen(dataSource, photo.id.toString())
        }

        when (selectedStyle) {
            DisplayStyle.Card ->
                PhotosCardListScreenContent(it, viewModel, clickListener)
            DisplayStyle.Grid ->
                PhotosGridScreenContent(it, mainViewModel, viewModel, clickListener)
        }
    }
}

@Composable
fun RowScope.DefaultTopBar(
    mainViewModel: MainViewModel,
    viewModel: PhotosListViewModel,
    openNavigationDrawer: () -> Unit
) {
    AppMenuButton(openNavigationDrawer)

    TitleBarText(
        text = stringResource(id = R.string.app_name),
        modifier = Modifier.weight(1f))

    ThemeSwitcher(mainViewModel = mainViewModel)
    DisplayStyleSelector(viewModel) { viewModel.updateDisplayStyle(it) }
}

@Composable
fun RowScope.SelectionTopBar(
    viewModel: PhotosListViewModel,
) {
    val selectedPhotos = viewModel.selectedPhotos.collectAsState()

    TitleBarButton(
        imageVector = Icons.Default.Clear,
        onClick = { viewModel.clearSelection() })

    TitleBarText(
        text = pluralStringResource(
            id = R.plurals.num_selected_photos,
            count = selectedPhotos.value.size,
            selectedPhotos.value.size),
        modifier = Modifier.weight(1f))

    val selectionStatus = viewModel.selectedPhotosStatus.collectAsState()
    val actionIcon = when (selectionStatus.value) {
        PhotoSelectionStatus.ALL_SAVED -> Icons.Default.Delete
        else -> ImageVector.vectorResource(R.drawable.ic_save)
    }
    TitleBarButton(
        imageVector = actionIcon,
        onClick = {
            when (selectionStatus.value) {
                PhotoSelectionStatus.ALL_SAVED -> viewModel.unSaveSelectedPhotos()
                else -> viewModel.saveSelectedPhotos()
            }
        })
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotosGridScreenContent(
    paddingValues: PaddingValues,
    mainViewModel: MainViewModel,
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
                val selectedItems by viewModel.selectedPhotos.collectAsState()
                val isSelected = selectedItems.contains(it)
                val isInSelectionMode = viewModel.selectedPhotos.value.isNotEmpty()

                val theme = mainViewModel.appTheme.collectAsState()
                val backgroundColor =
                    if (!isSelected) {
                        Color.Transparent
                    } else if (theme.value == AppTheme.Dark) {
                        Color.DarkGray
                    } else {
                        Color.LightGray
                    }

                Box(
                    modifier = Modifier
                        .background(backgroundColor)
                        .height(200.dp)) {
                    ItemPhotoCell(
                        photo = it,
                        isSelected = isSelected,
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    if (isInSelectionMode) {
                                        if (isSelected) {
                                            viewModel.unselectPhoto(it)
                                        } else {
                                            viewModel.selectPhoto(it)
                                        }
                                    } else {
                                        photoClickListener(it)
                                    }
                                },
                                onLongClick = {
                                    if (isInSelectionMode) {
                                        if (isSelected) {
                                            viewModel.unselectPhoto(it)
                                        } else {
                                            viewModel.selectPhoto(it)
                                        }
                                    } else {
                                        viewModel.selectPhoto(it)
                                    }
                                }
                            )
                            .padding(if (isSelected) 10.dp else (0.dp)))
                }
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