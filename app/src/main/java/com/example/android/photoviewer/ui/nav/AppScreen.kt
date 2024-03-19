package com.example.android.photoviewer.ui.nav

import com.example.android.photoviewer.ui.model.PhotosDataSource
import com.example.android.photoviewer.ui.nav.AppScreenParams.PHOTO_DATA_SOURCE

sealed class AppScreen(val route: String) {
    object HomeScreen : AppScreen(AppScreenName.HOME_SCREEN)
    object DetailsScreen : AppScreen("${AppScreenName.DETAILS_SCREEN}/{${AppScreenParams.PHOTO}}?$PHOTO_DATA_SOURCE={$PHOTO_DATA_SOURCE}")
    object SavedScreen : AppScreen(AppScreenName.SAVED_SCREEN)
}

object AppScreenName {
    const val HOME_SCREEN = "home_screen"
    const val DETAILS_SCREEN = "details_screen"
    const val SAVED_SCREEN = "saved_screen"

    fun detailsScreen(dataSource: PhotosDataSource, photoId: String): String =
        "$DETAILS_SCREEN/$photoId?$PHOTO_DATA_SOURCE=$dataSource"
}

object AppScreenParams {
    const val PHOTO = "photoId"
    const val PHOTO_DATA_SOURCE = "dataSource"
}