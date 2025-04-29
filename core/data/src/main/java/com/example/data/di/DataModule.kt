package com.example.data.di

import com.example.data.repositories.HomeRepository
import com.example.data.repositories.HomeRepositoryImpl
import com.example.network.HomeApi
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
    fun provideHomeRepository(api: HomeApi): HomeRepository {
        return HomeRepositoryImpl(api)
    }
}