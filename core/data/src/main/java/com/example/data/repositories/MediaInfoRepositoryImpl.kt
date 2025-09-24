package com.example.data.repositories

import com.example.data.model.MediaInfo
import com.example.network.api.MediaInfoApi
import javax.inject.Inject

class MediaInfoRepositoryImpl @Inject constructor (
    private val mediaInfoApi: MediaInfoApi
): MediaInfoRepository {
    override suspend fun fetchMediaInfo(id: Int): MediaInfo {
        val mediaInfo = mediaInfoApi.fetchMediaInfo(id)
        return MediaInfo(
            id = mediaInfo.id,
            title = mediaInfo.title,
            subTitle = mediaInfo.subTitle,
            bannerUrl = mediaInfo.bannerUrl,
            coverUrl = mediaInfo.coverUrl,
            score = mediaInfo.score,
            year = mediaInfo.year,
            status = mediaInfo.status,
            genres = mediaInfo.genres,
            episodes = mediaInfo.episodes,
            durationMinutes = mediaInfo.durationMinutes,
            studio = mediaInfo.studio,
            descriptionHtml = mediaInfo.descriptionHtml
        )
    }
}