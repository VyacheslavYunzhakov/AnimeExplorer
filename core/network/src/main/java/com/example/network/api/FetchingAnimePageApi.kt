package com.example.network.api

import com.example.network.model.MediaDto

interface FetchingAnimePageApi {
    suspend fun fetchAiringPage   (count: Int, page: Int): List<MediaDto>
    suspend fun fetchTrendingPage (count: Int, page: Int): List<MediaDto>
    suspend fun fetchUpcomingEpisodes (count: Int, page: Int): List<MediaDto>
}