package com.young.sportsmatch.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SearchPlaceList(
    var meta: PlaceMeta,
    var documents: List<Place>
)

@JsonClass(generateAdapter = true)
data class PlaceMeta(
    var total_count: Int,
    var pageable_count: Int,
    var is_end: Boolean,
    var same_name: RegionInfo,
)

@JsonClass(generateAdapter = true)
data class RegionInfo(
    var region: List<String>,
    var keyword: String,
    var selected_region: String,
)

@JsonClass(generateAdapter = true)
data class Place(
    var place_name: String,
    var address_name: String,
    var road_address_name: String,
    var x: String,
    var y: String,
)