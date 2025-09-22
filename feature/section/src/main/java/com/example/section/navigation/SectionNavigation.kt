package com.example.section.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.section.SectionScreen

import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import com.example.section.SectionType

@Serializable data class SectionRoute(val sectionType: SectionType)

fun NavController.navigateToSection(sectionType: SectionType) = navigate(route = SectionRoute(sectionType))

fun NavGraphBuilder.sectionScreen(
    onBackClick: () -> Unit,
    onMediaClick: (Int) -> Unit
) {
    composable<SectionRoute> { entry ->
        SectionScreen(
            onBackClick = onBackClick,
            onMediaClick = onMediaClick
        )
    }
}