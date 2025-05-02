package com.example.network

import javax.inject.Inject
import javax.inject.Singleton
import com.apollographql.apollo3.ApolloClient
import com.example.core.network.GetPageQuery
import com.example.network.model.MediaDto
import com.example.network.model.TitleDto

@Singleton
class HomeApiImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : HomeApi {
    override suspend fun fetchMediaPage(): List<MediaDto> {
        val response = apolloClient.query(GetPageQuery()).execute()
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