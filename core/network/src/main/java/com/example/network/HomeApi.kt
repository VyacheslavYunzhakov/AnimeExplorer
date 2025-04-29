package com.example.network

import com.example.network.model.MediaDto

interface HomeApi {
    suspend fun fetchMediaPage(): List<MediaDto>
}