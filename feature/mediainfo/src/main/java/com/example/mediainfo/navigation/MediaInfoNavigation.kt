package com.example.mediainfo.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.mediainfo.MediaInfoScreen
import kotlinx.serialization.Serializable

@Serializable data class MediaInfoRoute(val mediaId: Int)

fun NavController.navigateToMediaInfo(mediaId: Int) = navigate(route = MediaInfoRoute(mediaId))

fun NavGraphBuilder.mediaInfoScreen(
    onBackClick: () -> Unit
) {
    composable<MediaInfoRoute> { entry ->
        MediaInfoScreen(
            onBackClick = onBackClick
        )
    }
}