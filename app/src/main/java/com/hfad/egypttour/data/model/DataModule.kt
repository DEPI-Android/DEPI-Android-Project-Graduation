package com.hfad.egypttour.data.model

import android.content.Context
import com.hfad.egypttour.data.api.RetrofitInstance
import com.hfad.egypttour.data.repository.AuthRepository
import com.hfad.egypttour.data.repository.LandmarkRepository
import com.hfad.egypttour.data.session.SessionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideLandmarkRepository(
        @ApplicationContext context: Context
    ): LandmarkRepository {
        return LandmarkRepository(RetrofitInstance.api, context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context,
        sessionManager: SessionManager
    ): AuthRepository {
        return AuthRepository(context, sessionManager)
    }
}