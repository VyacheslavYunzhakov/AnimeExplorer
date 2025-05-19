package com.example.network.api

import com.example.network.model.MediaDto

interface FetchingAnimePageApi {
    suspend fun fetchAiringPage(): List<MediaDto>
    suspend fun fetchTrendingPage(): List<MediaDto>
    suspend fun fetchUpcomingPage(): List<MediaDto>
}