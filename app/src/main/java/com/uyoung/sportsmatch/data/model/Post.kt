package com.uyoung.sportsmatch.data.model

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
): java.io.Serializable {
    var isBookmarked = false
}

@JsonClass(generateAdapter = true)
data class MarkerPlace(
    var place_name: String,
    var x: String,
    var y: String,
)