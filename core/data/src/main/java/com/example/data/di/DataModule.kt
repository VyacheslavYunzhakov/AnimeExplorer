package com.example.data.di

import com.example.data.repositories.HomeRepository
import com.example.data.repositories.HomeRepositoryImpl
import com.example.network.api.AiringApi
import com.example.network.api.TrendingApi
import com.example.network.api.UpcomingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideHomeRepository(upcomingApi: UpcomingApi, trendingApi: TrendingApi, airingApi: AiringApi): HomeRepository {
        return HomeRepositoryImpl(upcomingApi, trendingApi, airingApi)
    }
}