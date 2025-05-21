package com.example.network.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.core.network.CurrentlyAiringAnimeQuery
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
    override suspend fun fetchAiringPage(count: Int, page: Int): List<MediaDto> {
        val response = apolloClient.query(CurrentlyAiringAnimeQuery(Optional.Present(count), Optional.Present(page))).execute()
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

    override suspend fun fetchTrendingPage(count: Int, page: Int): List<MediaDto> {
        val response = apolloClient.query(TrendingQuery(Optional.Present(count), Optional.Present(page))).execute()
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

    override suspend fun fetchUpcomingPage(count: Int, page: Int): List<MediaDto> {
        val response = apolloClient.query(NotYetReleasedQuery(Optional.Present(count), Optional.Present(page))).execute()
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