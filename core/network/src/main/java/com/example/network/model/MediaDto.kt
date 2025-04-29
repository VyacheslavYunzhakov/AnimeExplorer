package com.example.network.model

data class MediaDto(
    val id: Int,
    val bannerImage: String? = null,
    val averageScore: Int? = null,
    val title: TitleDto? = null
)

data class TitleDto(
    val english: String? = null
)

private data class PageResponse(
    val data: DataContainer
)

private data class DataContainer(
    val Page: PageContainer
)

private data class PageContainer(
    val media: List<MediaDto>
)

