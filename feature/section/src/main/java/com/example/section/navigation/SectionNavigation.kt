package com.example.section.navigation

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.example.section.SectionScreen

import kotlinx.serialization.Serializable
import androidx.navigation.compose.composable
import com.example.section.SectionType
import com.example.ui.ItemInfo
import kotlin.collections.set

@Serializable data class SectionRoute(val sectionType: SectionType)

fun NavController.navigateToSection(sectionType: SectionType) = navigate(route = SectionRoute(sectionType))

fun NavGraphBuilder.sectionScreen(
    onBackClick: () -> Unit,
    onMediaClick: (Int) -> Unit
) {
    composable<SectionRoute> { entry ->
        val itemPositions = remember { mutableStateMapOf<String, ItemInfo>() }
        SectionScreen(
            onBackClick = onBackClick,
            onMediaClick = onMediaClick,
            onItemPositioned = {id, imageUrl, rect ->
                itemPositions[id] = ItemInfo(id, imageUrl, rect)
            }
        )
    }
}