package com.example.android.photoviewer.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.android.photoviewer.R
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.theme.AppTheme

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