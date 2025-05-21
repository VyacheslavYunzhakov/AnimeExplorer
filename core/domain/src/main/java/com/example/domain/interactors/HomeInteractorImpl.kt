package com.example.domain.interactors

import com.example.data.model.Media
import com.example.data.repositories.HomeRepository
import javax.inject.Inject

class HomeInteractorImpl @Inject constructor(
    private val repository: HomeRepository
): HomeInteractor {
    override suspend fun getUpcomingPage(count: Int, page: Int): List<Media> = repository.getUpcomingPage(count, page)

    override suspend fun getTrendingPage(count: Int, page: Int): List<Media> = repository.getTrendingPage(count, page)

    override suspend fun getAiringPage(count: Int, page: Int): List<Media> = repository.getAiringPage(count, page)
}