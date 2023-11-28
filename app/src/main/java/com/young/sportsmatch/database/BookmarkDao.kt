package com.young.sportsmatch.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmark")
    suspend fun getAll(): List<BookmarkEntity>?

    @Insert
    suspend fun insertAll(posts: BookmarkEntity)

    @Delete
    suspend fun delete(posts: BookmarkEntity)
}