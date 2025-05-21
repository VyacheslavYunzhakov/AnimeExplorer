package com.example.data.repositories

import com.example.data.model.Media


interface HomeRepository {
    suspend fun getUpcomingPage(count: Int, page: Int): List<Media>
    suspend fun getTrendingPage(count: Int, page: Int): List<Media>
    suspend fun getAiringPage(count: Int, page: Int): List<Media>
}