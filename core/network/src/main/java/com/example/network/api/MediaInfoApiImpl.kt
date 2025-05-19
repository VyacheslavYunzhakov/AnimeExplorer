package com.example.network.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.core.network.GetMediaInfoQuery
import com.example.network.model.MediaInfoDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaInfoApiImpl @Inject constructor(
    private val apolloClient: ApolloClient
): MediaInfoApi {
    override suspend fun fetchMediaInfo(id: Int): MediaInfoDto {
    val response = apolloClient.query(GetMediaInfoQuery(id = Optional.Present(id))).execute()
        val mediaInfo = MediaInfoDto(
            response.data?.Media?.description,
            response.data?.Media?.coverImage?.large,
            response.data?.Media?.bannerImage
        )
        return mediaInfo
    }
}