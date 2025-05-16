package com.example.network.api

import com.example.network.model.MediaDto

interface TrendingApi {
    suspend fun fetchTrendingPage(): List<MediaDto>
}
