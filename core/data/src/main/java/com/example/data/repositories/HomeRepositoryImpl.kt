package com.example.data.repositories

import com.example.data.model.Media
import com.example.network.api.FetchingAnimePageApi
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val fetchingAnimePageApi: FetchingAnimePageApi
) : HomeRepository {

    override suspend fun getUpcomingPage(): List<Media> {
        val mediaList = fetchingAnimePageApi.fetchUpcomingPage()
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
        val mediaList = fetchingAnimePageApi.fetchTrendingPage()
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
        val mediaList = fetchingAnimePageApi.fetchAiringPage()
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