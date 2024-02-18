package com.uyoung.sportsmatch.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.uyoung.sportsmatch.data.model.Post
import dagger.hilt.android.scopes.ActivityScoped

@ActivityScoped
class Converter() {
    @TypeConverter
    fun postToString(post: Post?): String? {
        return Gson().toJson(post)
    }

    @TypeConverter
    fun stringToPost(postJson: String?): Post? {
        return Gson().fromJson(postJson, Post::class.java)
    }
}