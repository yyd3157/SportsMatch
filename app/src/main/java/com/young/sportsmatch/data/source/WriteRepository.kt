package com.young.sportsmatch.data.source

import com.young.sportsmatch.BuildConfig
import com.young.sportsmatch.data.model.MarkerPlace
import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.data.source.remote.MapRemoteDataSource
import com.young.sportsmatch.data.source.remote.PostRemoteDataSource
import com.young.sportsmatch.network.model.ApiResponse
import javax.inject.Inject

class WriteRepository @Inject constructor(
    private val mapRemoteDataSource: MapRemoteDataSource,
    private val postRemoteDataSource: PostRemoteDataSource,
) {

    suspend fun getMap(searchText: String): ApiResponse<SearchPlaceList> {
        val key = BuildConfig.KAKAO_API_KEY
        return mapRemoteDataSource.getMap(key, searchText)
    }

    suspend fun addPost(title: String, category: String, type: String, date: String, markerPlace: MarkerPlace, content: String): ApiResponse<Map<String, String>>? {
        return postRemoteDataSource.addPost(title, category, type, date, markerPlace, content)
        // 결과를 처리하는 코드 추가
    }
}