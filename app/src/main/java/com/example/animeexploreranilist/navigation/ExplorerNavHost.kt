package com.example.animeexploreranilist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.home.navigation.HomeBaseRoute
import com.example.home.navigation.homeScreen
import com.example.animeexploreranilist.ui.ExplorerAppState
import com.example.mediainfo.navigation.mediaInfoScreen
import com.example.mediainfo.navigation.navigateToMediaInfo

@Composable
fun ExplorerNavHost(
    appState: ExplorerAppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier
    ) {
        homeScreen(
            onMediaClick = { media ->
                navController.navigateToMediaInfo(media.id)
            }
        )
        mediaInfoScreen(
            onBackClick = navController::popBackStack
        )
    }
}