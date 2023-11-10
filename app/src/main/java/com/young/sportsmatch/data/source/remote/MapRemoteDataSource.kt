package com.young.sportsmatch.data.source.remote

import android.util.Log
import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.network.KakaoMapApiClient
import com.young.sportsmatch.network.model.ApiResponse
import javax.inject.Inject

class MapRemoteDataSource @Inject constructor(private val apiClient: KakaoMapApiClient) {

    suspend fun getMap(key: String, query: String): ApiResponse<SearchPlaceList> {
        Log.d("key", "$key")
        return apiClient.getSearchAddress(key, query)
    }
}
