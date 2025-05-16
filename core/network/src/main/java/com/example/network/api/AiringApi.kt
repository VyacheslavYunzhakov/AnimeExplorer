package com.example.network.api

import com.example.network.model.MediaDto

interface AiringApi {
    suspend fun fetchAiringPage(): List<MediaDto>
}
