package com.example.ui

import androidx.compose.ui.geometry.Rect

data class ItemInfo(
    val id: String,
    val imageUrl: Any,
    val boundsInWindow: Rect
)