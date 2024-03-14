package com.example.android.photoviewer.ui.nav

import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.photoviewer.R
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.model.PhotosListScreenType
import com.example.android.photoviewer.ui.photosdetails.PhotosDetailsScreen
import com.example.android.photoviewer.ui.photoslist.PhotosListScreen
import com.example.android.photoviewer.ui.saved.SavedPhotosListScreen
import kotlinx.coroutines.launch


@Composable
fun NavGraph(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: AppScreen.HomeScreen.route
    
    val menuItems = listOf(
        MenuItem(
            AppScreenName.HOME_SCREEN,
            stringResource(id = R.string.home),
            Icons.Default.Home),
        MenuItem(
            AppScreenName.SAVED_SCREEN,
            stringResource(id = R.string.saved),
            ImageVector.vectorResource(id = R.drawable.ic_saved))
    )

    val onItemClick: (MenuItem) -> Unit = {
        if (it.id != currentRoute) {
            navController.navigate(it.id)
        }
        coroutineScope.launch { drawerState.close() }
    }

    val openNavigationDrawer: () -> Unit = {
        coroutineScope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        drawerContent = {
            NavigationDrawer(
                route = currentRoute,
                modifier = Modifier.requiredWidth(300.dp),
                items = menuItems,
                onItemClick = onItemClick)
        }, 
        drawerState = drawerState 
    ) {
        NavGraphBody(
            mainViewModel = mainViewModel,
            navController = navController,
            openNavigationDrawer = openNavigationDrawer)
    }
}

@Composable
fun NavGraphBody(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    openNavigationDrawer: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.HomeScreen.route) {

        val navigateToDetailsScreen: (String) -> Unit = {
            navController.navigate(AppScreenName.detailsScreen(it))
        }

        composable(route = AppScreen.HomeScreen.route) {
            PhotosListScreen(
                mainViewModel = mainViewModel,
                screenType = PhotosListScreenType.HOME,
                openNavigationDrawer = openNavigationDrawer,
                navigateToDetailsScreen = navigateToDetailsScreen)
        }

        val navigateBack: () -> Unit = {
            navController.popBackStack()
        }
        
        composable(
            route = AppScreen.DetailsScreen.route,
            arguments = listOf(navArgument(AppScreenParams.PHOTO) { type = NavType.IntType })
        ) {
            val photoId = it.arguments?.getInt(AppScreenParams.PHOTO)
            PhotosDetailsScreen(
                mainViewModel,
                photoId = photoId,
                navigateBack)
        }

        composable(route = AppScreen.SavedScreen.route) {
            PhotosListScreen(
                mainViewModel = mainViewModel,
                screenType = PhotosListScreenType.SAVED,
                openNavigationDrawer = openNavigationDrawer,
                navigateToDetailsScreen = navigateToDetailsScreen)
        }
    }
}
