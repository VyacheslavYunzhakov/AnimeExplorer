package com.example.domain.interactors

import com.example.data.model.MediaInfo

interface MediaInfoInteractor {
    suspend fun getMediaInfo(id: Int): MediaInfo
}