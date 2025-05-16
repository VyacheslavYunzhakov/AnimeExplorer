package com.example.network.di

import com.apollographql.apollo3.ApolloClient
import com.example.network.api.AiringApi
import com.example.network.api.AiringApiImpl
import com.example.network.api.TrendingApi
import com.example.network.api.TrendingApiImpl
import com.example.network.api.UpcomingApi
import com.example.network.api.UpcomingApiImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApolloClient(): ApolloClient {
        return ApolloClient.Builder()
            .serverUrl("https://graphql.anilist.co")
            .build()
    }

    @Provides
    @Singleton
    fun provideUpcomingApi(apolloClient: ApolloClient): UpcomingApi {
        return UpcomingApiImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun provideTrendingApi(apolloClient: ApolloClient): TrendingApi {
        return TrendingApiImpl(apolloClient)
    }

    @Provides
    @Singleton
    fun provideAiringApi(apolloClient: ApolloClient): AiringApi {
        return AiringApiImpl(apolloClient)
    }
}