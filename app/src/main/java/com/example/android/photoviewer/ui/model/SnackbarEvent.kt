package com.example.android.photoviewer.ui.model

import androidx.compose.material3.SnackbarDuration

data class SnackbarEvent(
    val message: UiText,
    val actionLabel: UiText? = null,
    val duration: SnackbarDuration? = null,
    val onAction: (() -> Unit)? = null,
)
