package com.project.storyapp.di

import android.content.Context
import androidx.datastore.core.DataStore
import com.project.storyapp.data.local.UserPreferences
import com.project.storyapp.data.remote.api.ApiConfig
import com.project.storyapp.data.remote.api.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideAPIService() : ApiService {
        return ApiConfig.getApiService()
    }

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUserPreferences(dataStore: DataStore<Preferences>): UserPreferences {
        return UserPreferences.getInstance(dataStore)
    }
}