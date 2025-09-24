package com.example.data.model

data class MediaInfo (
    val id: Int?,
    val title: String,
    val subTitle: String?,
    val bannerUrl: String?,
    val coverUrl: String?,
    val score: Double?, // 0..10
    val year: Int?,
    val status: String?,
    val genres: List<String>,
    val episodes: Int?,
    val durationMinutes: Int?,
    val studio: String?,
    val descriptionHtml: String?
)