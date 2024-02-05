package com.example.android.photoviewer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppTheme {
    Light, Dark, Default
}

private val DarkColorScheme = darkColorScheme(
    primary = colorPrimary,
    onPrimary = colorWhite,
    secondary = colorSecondary,
    onSecondary = colorWhite,
    background = colorBlack,
    onBackground = colorWhite,
    surface = colorGrayDark,
    onSurface = colorWhite100
)

private val LightColorScheme = lightColorScheme(
    primary = colorPrimary,
    onPrimary = colorWhite,
    secondary = colorSecondary,
    onSecondary = colorWhite,
    background = colorWhite,
    onBackground = colorBlack,
    surface = colorWhite,
    onSurface = colorBlack100
)

@Composable
fun PhotoViewerTheme(
    appTheme: AppTheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when(appTheme) {
        AppTheme.Default -> {
            when {
                dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val context = LocalContext.current
                    if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }

                darkTheme -> DarkColorScheme
                else -> LightColorScheme
            }
        }
        AppTheme.Light -> {
            LightColorScheme
        }
        AppTheme.Dark -> {
            DarkColorScheme
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}