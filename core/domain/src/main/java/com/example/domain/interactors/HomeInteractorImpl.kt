package com.example.domain.interactors

import com.example.data.model.Media
import com.example.data.repositories.HomeRepository
import javax.inject.Inject

class HomeInteractorImpl @Inject constructor(
    private val repository: HomeRepository
): HomeInteractor {
    override suspend fun getMediaPage(): List<Media> =
        repository.getMediaPage()
}