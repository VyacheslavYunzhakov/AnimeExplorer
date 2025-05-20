package com.example.section


import androidx.compose.runtime.Composable


enum class SectionType {
    UPCOMING,
    TRENDING,
    CURRENTLY_AIRING
}

@Composable
fun SectionScreen(onBackClick: () -> Unit, onMediaClick: (Int) -> Unit) {

}