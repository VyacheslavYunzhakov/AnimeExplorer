package com.example.network.di

import com.apollographql.apollo3.ApolloClient
import com.example.network.HomeApi
import com.example.network.HomeApiImpl
import dagger.Binds
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
    fun provideHomeApi(apolloClient: ApolloClient): HomeApi {
        return HomeApiImpl(apolloClient)
    }
}