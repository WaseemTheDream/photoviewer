package com.example.android.photoviewer.ui.nav

sealed class AppScreen(val route: String) {
    object HomeScreen : AppScreen(AppScreenName.HOME_SCREEN)
    object DetailsScreen : AppScreen("${AppScreenName.DETAILS_SCREEN}/{${AppScreenParams.PHOTO}}")
    object SavedScreen : AppScreen(AppScreenName.SAVED_SCREEN)
}

object AppScreenName {
    const val HOME_SCREEN = "home_screen"
    const val DETAILS_SCREEN = "details_screen"
    const val SAVED_SCREEN = "saved_screen"

    fun detailsScreen(photoId: String): String = "$DETAILS_SCREEN/$photoId"
}

object AppScreenParams {
    const val PHOTO = "photoId"
}