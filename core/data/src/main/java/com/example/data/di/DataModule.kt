package com.example.data.di

import com.example.data.repositories.HomeRepository
import com.example.data.repositories.HomeRepositoryImpl
import com.example.data.repositories.MediaInfoRepository
import com.example.data.repositories.MediaInfoRepositoryImpl
import com.example.network.api.FetchingAnimePageApi
import com.example.network.api.MediaInfoApi
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
    fun provideHomeRepository(fetchingAnimePageApi: FetchingAnimePageApi): HomeRepository {
        return HomeRepositoryImpl(fetchingAnimePageApi)
    }

    @Provides
    @Singleton
    fun provideMediaInfoRepository(mediaInfoApi: MediaInfoApi): MediaInfoRepository {
        return MediaInfoRepositoryImpl(mediaInfoApi)
    }
}