package com.uyoung.sportsmatch.network

import com.uyoung.sportsmatch.data.model.SearchPlaceList
import com.uyoung.sportsmatch.network.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoMapApiClient {
    @GET("v2/local/search/keyword.json?page=1&size=10")
    suspend fun getSearchAddress(
        @Header("Authorization") key: String,
        @Query("query") query: String,
    ): ApiResponse<SearchPlaceList>
}