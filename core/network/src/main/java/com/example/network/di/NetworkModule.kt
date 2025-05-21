package com.example.network.di

import com.apollographql.apollo3.ApolloClient
import com.example.network.api.FetchingAnimePageApi
import com.example.network.api.FetchingAnimePageApiImpl
import com.example.network.api.MediaInfoApi
import com.example.network.api.MediaInfoApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .build()
    }

    @Provides
    fun provideUpcomingApi(apolloClient: ApolloClient): FetchingAnimePageApi {
        return FetchingAnimePageApiImpl(apolloClient)
    }

    @Provides
    fun provideMediaInfoApi(apolloClient: ApolloClient): MediaInfoApi {
        return MediaInfoApiImpl(apolloClient)
    }

}