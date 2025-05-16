package com.example.domain.interactors

import com.example.data.model.Media

interface HomeInteractor {
    suspend fun getUpcomingPage(): List<Media>
    suspend fun getTrendingPage(): List<Media>
    suspend fun getAiringPage(): List<Media>
}