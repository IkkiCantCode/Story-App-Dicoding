package com.ikki.storyapp.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.flow.first
import androidx.room.withTransaction


@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val apiService: ApiService,
    private val database: StoryDatabase,
    private val userRepository: UserRepository
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = getRemoteKeyForLastItem(state)
                remoteKey?.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        try {
            val token = userRepository.getSession().first().token
                ?: throw IllegalArgumentException("Token is required")

            val apiResponse = apiService.getStories(
                token = token,
                page = page,
                size = state.config.pageSize
            )

            val stories = apiResponse.listStory.map { story ->
                StoryEntity(
                    id = story.id ?: "",
                    name = story.name ?: "Unknown",
                    photoUrl = story.photoUrl ?: "",
                    description = story.description ?: "No description"
                )
            }

            val endOfPaginationReached = stories.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeysDao().clearRemoteKeys()
                    database.storyDao().clearStories()
                }
                val keys = stories.map {
                    RemoteKeys(
                        storyId = it.id,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                }
                database.remoteKeysDao().insertAll(keys)
                database.storyDao().insertAll(stories)
            }

            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull()?.data?.lastOrNull()?.let { story ->
            database.remoteKeysDao().remoteKeysByStoryId(story.id)
        }
    }
}