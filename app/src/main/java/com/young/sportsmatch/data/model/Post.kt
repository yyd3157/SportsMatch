package com.young.sportsmatch.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    val user: User,
    val title: String,
    val category: String,
    val type: String,
    val date: String,
    var markerPlace: MarkerPlace,
    val content: String,
): java.io.Serializable

@JsonClass(generateAdapter = true)
data class MarkerPlace(
    var place_name: String,
    var x: String,
    var y: String,
)