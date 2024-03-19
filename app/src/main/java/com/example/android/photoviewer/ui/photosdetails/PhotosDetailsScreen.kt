package com.example.android.photoviewer.ui.photosdetails

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableLongState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.android.photoviewer.R
import com.example.android.photoviewer.data.model.Photo
import com.example.android.photoviewer.ui.common.ErrorMessage
import com.example.android.photoviewer.ui.common.SystemBroadcastReceiver
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.common.ThemeSwitcher
import com.example.android.photoviewer.ui.model.PhotosDataSource
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosDetailsScreen(
    mainViewModel: MainViewModel,
    dataSource: PhotosDataSource,
    photoId: Int?,
    navigateBack: () -> Unit,
    viewModel: PhotosDetailsViewModel = hiltViewModel(),
) {
    LaunchedEffect(photoId, dataSource) {
        if (photoId != null) {
            viewModel.getPhoto(dataSource, photoId)
        }
    }

    val photo: Photo? by viewModel.photo.collectAsState()
    val downloadId = remember { mutableLongStateOf(-1) }
    val openDownloadDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
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
                MoreOptionsSelector(viewModel = viewModel, photo = photo, openDownloadDialog)
            }
        }
    ) {
        PhotosDetailsScreenContent(photo, paddingValues = it)
    }

    if (downloadId.value != (-1).toLong()) {
        val context = LocalContext.current
        SystemBroadcastReceiver(DownloadManager.ACTION_DOWNLOAD_COMPLETE) { intent ->
            val completedDownloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (completedDownloadId == downloadId.value) {
                Toast.makeText(context, R.string.download_completed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (openDownloadDialog.value && photo != null) {
        DownloadConfirmationDialog(
            photo = photo!!,
            openDownloadDialog = openDownloadDialog,
            downloadId = downloadId)
    }
}

@Composable
fun DownloadConfirmationDialog(
    photo: Photo,
    openDownloadDialog: MutableState<Boolean>,
    downloadId: MutableLongState
) {
    val activity = LocalContext.current as Activity
    val fileName = remember {
        mutableStateOf(
            URLUtil.guessFileName(photo.source.original, null, null)) 
    }
    AlertDialog(
        onDismissRequest = {
            openDownloadDialog.value = false
        },
        title = {
            Text(text = stringResource(id = R.string.download))
        },
        text = {
            Column {
                TextField(
                    value = fileName.value, 
                    onValueChange = { fileName.value = it })
                Text(text = stringResource(id = R.string.file_name))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    openDownloadDialog.value = false
                    downloadFile(activity, photo, fileName.value, downloadId)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary),
                enabled = !fileName.value.isNullOrBlank()) {
                Text(text = stringResource(id = R.string.download))
            }
        },
        dismissButton = {
            Button(
                onClick = { openDownloadDialog.value = false },
                colors = ButtonDefaults.outlinedButtonColors()
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
        })
}

@Composable
fun MoreOptionsSelector(
    viewModel: PhotosDetailsViewModel,
    photo: Photo?,
    openDownloadDialog: MutableState<Boolean>
) {
    if (photo == null) {
        return
    }

    val activity = LocalContext.current as Activity
    var expanded by  remember { mutableStateOf(false) }

    val shareAction: () -> Unit = {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.share_text, photo.url))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        activity.startActivity(shareIntent)
        expanded = false
    }

    val isSaved by viewModel.isSaved.collectAsState()

    IconButton(onClick = { expanded = true }) {
        Icon(
            painter = painterResource(id = R.drawable.ic_vertical_menu),
            contentDescription = stringResource(id = R.string.more_options),
            tint = MaterialTheme.colorScheme.onPrimary)
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.share)) },
                onClick = shareAction)
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.download)) },
                onClick = {
                    expanded = false
                    openDownloadDialog.value = true
                })
            DropdownMenuItem(
                text = {
                    val saveTextRes = if (isSaved) R.string.unsave else R.string.save
                    Text(text = stringResource(id = saveTextRes))
                },
                onClick = {
                    expanded = false
                    if (isSaved) {
                        viewModel.unSavePhoto(photo)
                    } else {
                        viewModel.savePhoto(photo)
                    }
                })
        }
    }
}

@Composable
fun PhotosDetailsScreenContent(
    photo: Photo?,
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier.padding(paddingValues),
    ) {
        if (photo == null) {
            ErrorMessage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                message = stringResource(id = R.string.photo_not_found))
            return
        }

        val zoomState = rememberZoomState()
        val placeholderImagePainter = rememberAsyncImagePainter(model = photo.source.medium)

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(zoomState = zoomState),
            model = photo.source.original,
            placeholder = placeholderImagePainter,
            contentDescription = photo.description,
            contentScale = ContentScale.Crop,
            onSuccess = { state ->
                zoomState.setContentSize(state.painter.intrinsicSize)
            })
    }
}

private fun downloadFile(
    activity: Activity,
    photo: Photo,
    fileName: String,
    downloadId: MutableLongState
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
        ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_DENIED) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1)
        Toast.makeText(
            activity,
            R.string.permission_denied,
            Toast.LENGTH_LONG
        ).show()
        return
    }

    val url = photo.source.original
    val request = DownloadManager.Request(Uri.parse(url))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)

    val downloadManager = activity.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    val newDownloadId = downloadManager.enqueue(request)

    Toast.makeText(activity, R.string.download_started, Toast.LENGTH_SHORT).show()
    downloadId.value = newDownloadId
}