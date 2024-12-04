package com.ikki.storyapp.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.ikki.storyapp.data.ApiConfig
import com.ikki.storyapp.data.AuthRepository
import com.ikki.storyapp.data.StoryRepository
import com.ikki.storyapp.data.UserRepository
import com.ikki.storyapp.data.pref.UserPreference
import com.ikki.storyapp.data.pref.dataStore

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val apiService = ApiConfig.getApiService()
        return AuthRepository(apiService)
    }

    fun provideUserRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(userPreference)
    }

    fun provideRepository(context: Context): StoryRepository {
        val userRepository = provideUserRepository(context)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, userRepository)
    }
}