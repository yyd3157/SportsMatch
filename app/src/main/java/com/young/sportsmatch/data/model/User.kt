package com.young.sportsmatch.data.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class User(
    val nickName: String,
    val imageUrl: String,
)