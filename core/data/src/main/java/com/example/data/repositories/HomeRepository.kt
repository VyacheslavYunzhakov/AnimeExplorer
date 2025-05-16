package com.example.data.repositories

import com.example.data.model.Media


interface HomeRepository {
    suspend fun getUpcomingPage(): List<Media>
    suspend fun getTrendingPage(): List<Media>
    suspend fun getAiringPage(): List<Media>
}