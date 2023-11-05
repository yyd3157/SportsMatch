package com.young.sportsmatch.network

import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.network.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoMapApiClient {
    @GET("v2/local/search/keyword.json?page=3&size=3")
    suspend fun getSearchAddress(
        @Header("Authorization") key: String,
        @Query("query") query: String,
    ): ApiResponse<SearchPlaceList>
}