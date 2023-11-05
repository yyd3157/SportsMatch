package com.young.sportsmatch.data.source

import com.young.sportsmatch.BuildConfig
import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.data.source.remote.MapRemoteDataSource
import com.young.sportsmatch.network.model.ApiResponse
import javax.inject.Inject

class WriteRepository @Inject constructor(
    private val remoteDataSource: MapRemoteDataSource,
) {

    suspend fun getMap(searchText: String): ApiResponse<SearchPlaceList> {
        val key = BuildConfig.KAKAO_API_KEY
        return remoteDataSource.getMap(key, searchText)
    }
}