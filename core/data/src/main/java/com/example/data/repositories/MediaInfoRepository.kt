package com.example.data.repositories

import com.example.data.model.MediaInfo

interface MediaInfoRepository {
    suspend fun fetchMediaInfo(id: Int): MediaInfo
}