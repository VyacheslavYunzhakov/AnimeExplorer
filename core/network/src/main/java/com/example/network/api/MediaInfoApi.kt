package com.example.network.api

import com.example.network.model.MediaInfoDto

interface MediaInfoApi {
    suspend fun fetchMediaInfo(id: Int): MediaInfoDto
}