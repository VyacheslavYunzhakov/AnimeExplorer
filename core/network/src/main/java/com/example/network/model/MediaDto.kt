package com.example.network.model

data class MediaDto(
    val id: Int,
    val coverImage: String? = null,
    val averageScore: Int? = null,
    val title: TitleDto? = null
)

class TitleDto(
    val english: String? = null
)
