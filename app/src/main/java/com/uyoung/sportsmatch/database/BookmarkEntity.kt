package com.uyoung.sportsmatch.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.uyoung.sportsmatch.data.model.Post
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
@Entity(tableName = "bookmark")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val post: Post,
    val category: String?,
)
