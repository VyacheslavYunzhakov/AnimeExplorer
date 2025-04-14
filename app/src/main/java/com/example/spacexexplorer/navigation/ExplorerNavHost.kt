package com.example.spacexexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.spacexexplorer.ui.ExplorerAppState

@Composable
fun ExplorerNavHost(
    appState: ExplorerAppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        homeScreen(
            onLaunchClick = { /*launchId ->
                navController.navigateToLaunchDetails(launchId)*/
            }
        )
    }
}