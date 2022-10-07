package com.project.core.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.core.data.source.local.entity.StoryEntity
import com.project.core.data.source.local.entity.StoryRemoteKeys
import com.project.core.data.source.local.room.StoryDao
import com.project.core.data.source.local.room.StoryRemoteKeysDao

@Database(entities = [StoryEntity::class, StoryRemoteKeys::class], version = 2, exportSchema = false)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): StoryRemoteKeysDao
}