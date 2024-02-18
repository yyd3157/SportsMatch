package com.uyoung.sportsmatch.data.source.remote

import android.util.Log
import com.uyoung.sportsmatch.data.model.SearchPlaceList
import com.uyoung.sportsmatch.network.KakaoMapApiClient
import com.uyoung.sportsmatch.network.model.ApiResponse
import javax.inject.Inject

class MapRemoteDataSource @Inject constructor(private val apiClient: KakaoMapApiClient) {

    suspend fun getMap(key: String, query: String): ApiResponse<SearchPlaceList> {
        Log.d("key", "$key")
        return apiClient.getSearchAddress(key, query)
    }
}
