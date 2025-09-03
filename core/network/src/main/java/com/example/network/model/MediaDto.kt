package com.example.network.model

data class MediaDto(
    val id: Int,
    val coverImage: String? = null,
    val averageScore: Int? = null,
    val title: TitleDto? = null,
    val episode: Int? = null,
    val timeUntilAiring: Int? = null
)

class TitleDto(
    val english: String? = null,
    val userPreferred: String? = null,
    val romaji: String? = null,
    val native: String? = null,
    val display: String? = null
)
