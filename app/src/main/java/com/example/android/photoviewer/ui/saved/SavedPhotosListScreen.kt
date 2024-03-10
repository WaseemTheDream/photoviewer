package com.example.android.photoviewer.ui.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.android.photoviewer.R
import com.example.android.photoviewer.ui.common.AppBarTitle
import com.example.android.photoviewer.ui.common.AppMenuButton
import com.example.android.photoviewer.ui.common.ErrorMessage
import com.example.android.photoviewer.ui.common.ThemeSwitcher
import com.example.android.photoviewer.ui.main.MainViewModel

@Composable
fun SavedPhotosListScreen(
    mainViewModel: MainViewModel,
    openNavigationDrawer: () -> Unit
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
                AppMenuButton(openNavigationDrawer)

                AppBarTitle(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.weight(1f))

                ThemeSwitcher(mainViewModel = mainViewModel)
            }
        }
    ) {
        SavedPhotosListScreenContent(paddingValues = it)
    }
}

@Composable
fun SavedPhotosListScreenContent(
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier.padding(paddingValues)
    ) {
        ErrorMessage(message = "TODO / Not Implemented")
    }
}