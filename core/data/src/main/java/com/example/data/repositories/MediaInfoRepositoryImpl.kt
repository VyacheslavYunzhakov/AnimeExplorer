package com.example.data.repositories

import com.example.data.model.MediaInfo
import com.example.network.api.MediaInfoApi
import javax.inject.Inject

class MediaInfoRepositoryImpl @Inject constructor (
    private val mediaInfoApi: MediaInfoApi
): MediaInfoRepository {
    override suspend fun fetchMediaInfo(id: Int): MediaInfo {
        val mediaInfo = mediaInfoApi.fetchMediaInfo(id)
        return MediaInfo (
            mediaInfo.description,
            mediaInfo.coverImage,
            mediaInfo.bannerImage
        )
    }
}