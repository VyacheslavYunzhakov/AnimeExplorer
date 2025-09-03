package com.example.network.api

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.example.core.network.CurrentlyAiringAnimeQuery
import com.example.core.network.TrendingQuery
import com.example.core.network.UpcomingEpisodesQuery
import com.example.network.model.MediaDto
import com.example.network.model.TitleDto
import javax.inject.Inject

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
                        title           = TitleDto(
                            english = it.title?.english.orEmpty()
                        )
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
                        title           = TitleDto(
                            english = it.title?.english.orEmpty()
                        )
                    )
                }
            }
            ?: emptyList()
        return mediaList
    }

    override suspend fun fetchUpcomingEpisodes(count: Int, page: Int): List<MediaDto> {
        val response = apolloClient
            .query(UpcomingEpisodesQuery(
                page = Optional.Present(page),
                perPage = Optional.Present(count)
            ))
            .execute()

        val mediaList = response.data
            ?.Page
            ?.airingSchedules
            ?.mapNotNull { schedule ->
                val media = schedule?.media ?: return@mapNotNull null

                val titleObj = media.title
                val titleText = titleObj?.userPreferred
                    ?: titleObj?.english
                    ?: titleObj?.romaji
                    ?: titleObj?.native
                    ?: media.synonyms?.firstOrNull()
                    ?: ""

                val score = when {
                    media.averageScore != null && media.averageScore != 0 -> media.averageScore
                    media.meanScore != null && media.meanScore != 0 -> media.meanScore
                    else -> null
                }

                MediaDto(
                    id = media.id,
                    coverImage = media.coverImage?.large,
                    averageScore = score,
                    title = TitleDto(
                        userPreferred = titleObj?.userPreferred.orEmpty(),
                        english = titleObj?.english.orEmpty(),
                        romaji = titleObj?.romaji.orEmpty(),
                        native = titleObj?.native.orEmpty(),
                        display = titleText // готовое название для UI
                    ),
                    episode = schedule.episode,
                    timeUntilAiring = schedule.timeUntilAiring
                )
            }
            ?: emptyList()

        return mediaList
    }
}