package com.example.animeexploreranilist.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.home.navigation.HomeBaseRoute
import com.example.home.navigation.homeScreen
import com.example.animeexploreranilist.ui.ExplorerAppState
import com.example.home.MediaSectionType
import com.example.mediainfo.navigation.mediaInfoScreen
import com.example.mediainfo.navigation.navigateToMediaInfo
import com.example.section.SectionType
import com.example.section.navigation.navigateToSection
import com.example.section.navigation.sectionScreen

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
            onShowAllClick = { section ->
                when (section) {
                    MediaSectionType.UPCOMING -> navController.navigateToSection(SectionType.UPCOMING)
                    MediaSectionType.TRENDING -> navController.navigateToSection(SectionType.TRENDING)
                    MediaSectionType.CURRENTLY_AIRING -> navController.navigateToSection(SectionType.CURRENTLY_AIRING)
                }
            },
            onMediaClick = { id ->
                navController.navigateToMediaInfo(id)
            }
        )
        mediaInfoScreen(
            onBackClick = navController::popBackStack
        )
        sectionScreen(
            onBackClick = navController::popBackStack,
            onMediaClick = { id ->
                navController.navigateToMediaInfo(id)
            }
        )
    }
}