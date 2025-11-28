package com.hfad.egypttour.data.di

import com.hfad.egypttour.data.api.RetrofitInstance
import com.hfad.egypttour.data.repository.LandmarkRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt dependency injection module for data layer
 * Provides singletons for API and Repository
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLandmarkRepository(): LandmarkRepository {
        return LandmarkRepository(RetrofitInstance.api)
    }
}

