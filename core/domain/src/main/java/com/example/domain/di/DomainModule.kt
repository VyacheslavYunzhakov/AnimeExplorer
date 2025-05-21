package com.example.domain.di

import com.example.data.repositories.HomeRepository
import com.example.data.repositories.MediaInfoRepository
import com.example.domain.interactors.HomeInteractor
import com.example.domain.interactors.HomeInteractorImpl
import com.example.domain.interactors.MediaInfoInteractor
import com.example.domain.interactors.MediaInfoInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    fun provideHomeInteractor(repository: HomeRepository): HomeInteractor {
        return HomeInteractorImpl(repository)
    }

    @Provides
    fun provideMediaInfoInteractor(repository: MediaInfoRepository): MediaInfoInteractor {
        return MediaInfoInteractorImpl(repository)
    }

}