package com.example.domain.interactors

import com.example.data.model.MediaInfo
import com.example.data.repositories.MediaInfoRepository
import javax.inject.Inject

class MediaInfoInteractorImpl @Inject constructor(
    private val repository: MediaInfoRepository
): MediaInfoInteractor {
    override suspend fun getMediaInfo(id: Int): MediaInfo {
        return repository.fetchMediaInfo(id)
    }
}