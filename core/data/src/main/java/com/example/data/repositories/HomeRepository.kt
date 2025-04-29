package com.example.data.repositories

import com.example.data.model.Media


interface HomeRepository {
    suspend fun getMediaPage(): List<Media>
}