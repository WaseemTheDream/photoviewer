package com.example.android.photoviewer.ui.nav

import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.android.photoviewer.R
import com.example.android.photoviewer.ui.main.MainViewModel
import com.example.android.photoviewer.ui.model.PhotosDataSource
import com.example.android.photoviewer.ui.photosdetails.PhotosDetailsScreen
import com.example.android.photoviewer.ui.photoslist.PhotosListScreen
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
            navController.popBackStack()
            navController.navigate(it.id)
        }
        coroutineScope.launch { drawerState.close() }
    }

    val openNavigationDrawer: () -> Unit = {
        coroutineScope.launch { drawerState.open() }
    }

    ModalNavigationDrawer(
        gesturesEnabled = drawerState.isOpen,
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

        val navigateToDetailsScreen: (PhotosDataSource, String) -> Unit = { dataSource, photoId ->
            navController.navigate(AppScreenName.detailsScreen(dataSource, photoId))
        }

        composable(route = AppScreen.HomeScreen.route) {
            PhotosListScreen(
                mainViewModel = mainViewModel,
                dataSource = PhotosDataSource.HOME,
                openNavigationDrawer = openNavigationDrawer,
                navigateToDetailsScreen = navigateToDetailsScreen)
        }

        val navigateBack: () -> Unit = {
            navController.popBackStack()
        }
        
        composable(
            route = AppScreen.DetailsScreen.route,
            arguments = listOf(
                navArgument(AppScreenParams.PHOTO) { type = NavType.IntType },
                navArgument(AppScreenParams.PHOTO_DATA_SOURCE) { type = NavType.StringType }
            )
        ) {
            val photoId = it.arguments?.getInt(AppScreenParams.PHOTO)
            val dataSource = it.arguments?.getString(AppScreenParams.PHOTO_DATA_SOURCE)
                ?.let { source -> PhotosDataSource.valueOf(source) }
                ?: throw IllegalArgumentException("Missing required data source argument")
            PhotosDetailsScreen(
                mainViewModel = mainViewModel,
                photoId = photoId,
                dataSource = dataSource,
                navigateBack = navigateBack)
        }

        composable(route = AppScreen.SavedScreen.route) {
            PhotosListScreen(
                mainViewModel = mainViewModel,
                dataSource = PhotosDataSource.SAVED,
                openNavigationDrawer = openNavigationDrawer,
                navigateToDetailsScreen = navigateToDetailsScreen)
        }
    }
}
