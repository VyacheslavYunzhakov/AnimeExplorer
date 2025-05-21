package com.example.data.repositories

import com.example.data.model.Media
import com.example.network.api.FetchingAnimePageApi
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val fetchingAnimePageApi: FetchingAnimePageApi
) : HomeRepository {

    override suspend fun getUpcomingPage(count: Int, page: Int): List<Media> {
        val mediaList = fetchingAnimePageApi.fetchUpcomingPage(count, page)
        return mediaList.map { dto ->
            Media(
                id = dto.id,
                coverImage = dto.coverImage,
                title = dto.title?.english,
                averageScore = dto.averageScore
            )
        }
    }

    override suspend fun getTrendingPage(count: Int, page: Int): List<Media> {
        val mediaList = fetchingAnimePageApi.fetchTrendingPage(count, page)
        return mediaList.map { dto ->
            Media(
                id = dto.id,
                coverImage = dto.coverImage,
                title = dto.title?.english,
                averageScore = dto.averageScore
            )
        }
    }

    override suspend fun getAiringPage(count: Int, page: Int): List<Media> {
        val mediaList = fetchingAnimePageApi.fetchAiringPage(count, page)
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