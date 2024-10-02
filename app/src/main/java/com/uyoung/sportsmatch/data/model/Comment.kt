package com.uyoung.sportsmatch.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Comment(
    val postId: String,
    val nickname: String,
    val commentContent: String,
    val timeStamp: String,
)