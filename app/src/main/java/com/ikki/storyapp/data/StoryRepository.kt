package com.ikki.storyapp.data

import com.ikki.storyapp.data.pref.UserModel
import com.ikki.storyapp.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryRepository(private val apiService: ApiService, private val userRepository: UserRepository) {

    suspend fun getStories(token: String): List<ListStoryItem> {
        val response = apiService.getStories(token)
        if (response.error == true) {
            throw Exception(response.message ?: "Unknown error occurred")
        }
        return response.listStory
    }


    suspend fun logout() {
        userRepository.logout()
    }

    suspend fun getSession(): UserModel {
        return userRepository.getSession().first()
    }

    companion object {
        private var instance: StoryRepository? = null
        fun getInstance(apiService: ApiService, userRepository: UserRepository): StoryRepository {
            if (instance == null) {
                instance = StoryRepository(apiService, userRepository)
            }
            return instance!!
        }
    }
}