package com.example.domain.interactors

import com.example.data.model.Media

interface HomeInteractor {
    suspend fun getUpcomingPage(count: Int, page: Int = 1): List<Media>
    suspend fun getTrendingPage(count: Int, page: Int = 1): List<Media>
    suspend fun getAiringPage(count: Int, page: Int = 1): List<Media>
}