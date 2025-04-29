package com.example.home.navigation


import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.home.HomeScreen
import com.example.home.MediaUiModel
import kotlinx.serialization.Serializable

@Serializable data object HomeRoute

@Serializable data object HomeBaseRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavGraphBuilder.homeScreen(
    onMediaClick: (MediaUiModel) -> Unit
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(onMediaClick)
        }
    }
}