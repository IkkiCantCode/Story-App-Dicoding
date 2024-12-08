package com.ikki.storyapp.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stories: List<StoryEntity>)

    @Query("SELECT * FROM stories")
    fun getStories(): PagingSource<Int, StoryEntity>

    @Query("SELECT * FROM stories")
    suspend fun getStoriesAsList(): List<StoryEntity>

    @Query("DELETE FROM stories")
    suspend fun clearStories()
}