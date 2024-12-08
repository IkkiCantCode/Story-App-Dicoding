package com.ikki.storyapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stories")
data class StoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val photoUrl: String,
    val description: String
)

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val storyId: String,
    val prevKey: Int?,
    val nextKey: Int?
)
