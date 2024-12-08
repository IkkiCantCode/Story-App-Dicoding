package com.ikki.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.map
import com.ikki.storyapp.data.pref.UserModel
import com.ikki.storyapp.response.ListStoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val userRepository: UserRepository,
    private val database: StoryDatabase
) {
    fun getPagedStories(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).flow
    }

    suspend fun logout() {
        userRepository.logout()
    }

    suspend fun getSession(): UserModel {
        return userRepository.getSession().first()
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            userRepository: UserRepository,
            database: StoryDatabase
        ): StoryRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, userRepository, database).also { instance = it }
            }
        }
    }
}