package com.example.domain.interactors

import com.example.data.model.Media

interface HomeInteractor {
    suspend fun getMediaPage(): List<Media>
}