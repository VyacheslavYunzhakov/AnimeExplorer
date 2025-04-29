package com.example.domain.interactors.di

import com.example.data.repositories.HomeRepository
import com.example.domain.interactors.HomeInteractor
import com.example.domain.interactors.HomeInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideHomeInteractor(repository: HomeRepository): HomeInteractor {
        return HomeInteractorImpl(repository)
    }

}