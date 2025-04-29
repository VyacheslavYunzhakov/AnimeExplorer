package com.example.animeexploreranilist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

class ExplorerAppState(
    val navController: NavHostController
) {
    // Add common navigation helpers if needed, e.g.:
    // fun navigateToTopLevelDestination(route: String) { ... }
}

@Composable
fun rememberExplorerAppState(
    navController: NavHostController = rememberNavController()
): ExplorerAppState {
    return remember(navController) {
        ExplorerAppState(navController)
    }
}
