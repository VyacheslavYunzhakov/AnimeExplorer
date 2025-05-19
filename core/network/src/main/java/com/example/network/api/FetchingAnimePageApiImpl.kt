package com.example.network.api

import com.apollographql.apollo3.ApolloClient
import com.example.core.network.NotYetReleasedQuery
import com.example.core.network.TrendingQuery
import com.example.network.model.MediaDto
import com.example.network.model.TitleDto
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class FetchingAnimePageApiImpl @Inject constructor(
    private val apolloClient: ApolloClient
):FetchingAnimePageApi {
    override suspend fun fetchAiringPage(): List<MediaDto> {
        val response = apolloClient.query(TrendingQuery()).execute()
        val mediaList = return response.data
            ?.Page
            ?.media
            ?.mapNotNull { media ->
                media?.let {
                    MediaDto(
                        id              = it.id,
                        coverImage  = it.coverImage?.large,
                        averageScore    = it.averageScore,
                        title           = TitleDto(english = it.title?.english.orEmpty())
                    )
                }
            }
            ?: emptyList()
        return mediaList
    }

    override suspend fun fetchTrendingPage(): List<MediaDto> {
        val response = apolloClient.query(TrendingQuery()).execute()
        val mediaList = return response.data
            ?.Page
            ?.media
            ?.mapNotNull { media ->
                media?.let {
                    MediaDto(
                        id              = it.id,
                        coverImage  = it.coverImage?.large,
                        averageScore    = it.averageScore,
                        title           = TitleDto(english = it.title?.english.orEmpty())
                    )
                }
            }
            ?: emptyList()
        return mediaList
    }

    override suspend fun fetchUpcomingPage(): List<MediaDto> {
        val response = apolloClient.query(NotYetReleasedQuery()).execute()
        val mediaList = return response.data
            ?.Page
            ?.media
            ?.mapNotNull { media ->
                media?.let {
                    MediaDto(
                        id              = it.id,
                        coverImage  = it.coverImage?.large,
                        averageScore    = it.averageScore,
                        title           = TitleDto(english = it.title?.english.orEmpty())
                    )
                }
            }
            ?: emptyList()
        return mediaList
    }
}