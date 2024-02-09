package com.example.android.photoviewer.ui.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.photoslist.PhotosListScreen

@Composable
fun NavGraph(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route) {

        val navigateToDetailsScreen: (String) -> Unit = {
            navController.navigate(AppScreenName.detailsScreen(it))
        }

        composable(route = AppScreen.HomeScreen.route) {
            PhotosListScreen(
                mainViewModel = mainViewModel,
                navigateToDetailsScreen = navigateToDetailsScreen)
        }
        
        composable(
            route = AppScreen.DetailsScreen.route,
            arguments = listOf(navArgument(AppScreenParams.PHOTO) { type = NavType.IntType })
        ) {
            val photoId = it.arguments?.getInt(AppScreenParams.PHOTO)
            Text(text = "Details Screen: $photoId")
        }
    }
}
