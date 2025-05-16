package com.example.network.api

import com.example.network.model.MediaDto

interface UpcomingApi {
    suspend fun fetchUpcomingPage(): List<MediaDto>
}