package com.example.data.repositories

import com.example.data.model.Media
import com.example.network.api.AiringApi
import com.example.network.api.TrendingApi
import com.example.network.api.UpcomingApi
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val upcomingApi: UpcomingApi,
    private val trendingApi: TrendingApi,
    private val airingApi: AiringApi
) : HomeRepository {

    override suspend fun getUpcomingPage(): List<Media> {
        val mediaList = upcomingApi.fetchUpcomingPage()
        return mediaList.map { dto ->
            Media(
                id = dto.id,
                coverImage = dto.coverImage,
                title = dto.title?.english,
                averageScore = dto.averageScore
            )
        }
    }

    override suspend fun getTrendingPage(): List<Media> {
        val mediaList = trendingApi.fetchTrendingPage()
        return mediaList.map { dto ->
            Media(
                id = dto.id,
                coverImage = dto.coverImage,
                title = dto.title?.english,
                averageScore = dto.averageScore
            )
        }
    }

    override suspend fun getAiringPage(): List<Media> {
        val mediaList = airingApi.fetchAiringPage()
        return mediaList.map { dto ->
            Media(
                id = dto.id,
                coverImage = dto.coverImage,
                title = dto.title?.english,
                averageScore = dto.averageScore
            )
        }
    }
}