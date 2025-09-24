package com.example.network.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.core.network.MediaDetailsQuery
import com.example.network.model.MediaInfoDto
import javax.inject.Inject

class MediaInfoApiImpl @Inject constructor(
    private val apolloClient: ApolloClient
): MediaInfoApi {
    override suspend fun fetchMediaInfo(id: Int): MediaInfoDto {
    val response = apolloClient.query(MediaDetailsQuery(id = Optional.Present(id))).execute()
        val media = response.data?.Media
        val titleObj = media?.title
        val displayTitle = titleObj?.userPreferred
            ?: titleObj?.english
            ?: titleObj?.romaji
            ?: titleObj?.native
            ?: ""

        val score100 = when {
            media?.averageScore != null && media.averageScore != 0 -> media.averageScore
            media?.meanScore != null && media.meanScore != 0 -> media.meanScore
            else -> null
        }
        val scoreOutOf10: Double? = score100?.let { it / 10.0 }

        val studioName = media?.studios?.nodes?.firstOrNull { it?.isAnimationStudio == true }?.name
            ?: media?.studios?.nodes?.firstOrNull()?.name
            ?: ""

        return MediaInfoDto(
            id = media?.id,
            title = displayTitle,
            subTitle = null,
            bannerUrl = media?.bannerImage,
            coverUrl = media?.coverImage?.large ?: media?.coverImage?.medium,
            score = scoreOutOf10,
            year = media?.startDate?.year,
            status = media?.status.toString(),
            genres = media?.genres?.map { it.toString() } ?: emptyList(),
            episodes = media?.episodes,
            durationMinutes = media?.duration,
            studio = studioName,
            descriptionHtml = media?.description
        )
    }
}