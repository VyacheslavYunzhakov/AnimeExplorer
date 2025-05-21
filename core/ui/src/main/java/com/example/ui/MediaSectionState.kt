package com.example.ui

data class MediaSectionState (
    val media: List<MediaUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)