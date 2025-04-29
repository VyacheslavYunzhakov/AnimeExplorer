package com.example.data.repositories

import com.example.data.model.Media
import com.example.network.HomeApi
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val api: HomeApi
) : HomeRepository {

    override suspend fun getMediaPage(): List<Media> {
        // Assume HomeApi.fetchLaunches() returns List<LaunchDto>
        val mediaList = api.fetchMediaPage()
        return mediaList.map { dto ->
            Media(
                id = dto.id,
                bannerImage = dto.bannerImage,
                title = dto.title?.english,
                averageScore = dto.averageScore
            )
        }
    }
}