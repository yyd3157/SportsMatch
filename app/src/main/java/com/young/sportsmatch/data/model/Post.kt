package com.young.sportsmatch.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    val user: User,
    val title: String,
    val category: String,
    val date: String,
    var place: Place,
)