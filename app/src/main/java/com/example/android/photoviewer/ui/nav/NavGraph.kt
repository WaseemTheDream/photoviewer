package com.example.android.photoviewer.ui.nav

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.photoslist.PhotosListScreen

@Composable
fun NavGraph(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route) {

        composable(route = AppScreen.HomeScreen.route) {
            PhotosListScreen(
                mainViewModel = mainViewModel,
                navController = navController)
        }
        
        composable(route = AppScreen.DetailsScreen.route) {
            Text(text = "Details Screen")
        }
    }
}
