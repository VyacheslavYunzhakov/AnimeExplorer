package com.example.domain.interactors

import com.example.data.model.Media
import com.example.data.repositories.HomeRepository
import javax.inject.Inject

class HomeInteractorImpl @Inject constructor(
    private val repository: HomeRepository
): HomeInteractor {
    override suspend fun getUpcomingPage(): List<Media> = repository.getUpcomingPage()

    override suspend fun getTrendingPage(): List<Media> = repository.getTrendingPage()

    override suspend fun getAiringPage(): List<Media> = repository.getAiringPage()
}