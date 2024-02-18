package com.uyoung.sportsmatch.data.source

import com.uyoung.sportsmatch.BuildConfig
import com.uyoung.sportsmatch.data.model.MarkerPlace
import com.uyoung.sportsmatch.data.model.SearchPlaceList
import com.uyoung.sportsmatch.data.source.remote.MapRemoteDataSource
import com.uyoung.sportsmatch.data.source.remote.PostRemoteDataSource
import com.uyoung.sportsmatch.network.extentions.onError
import com.uyoung.sportsmatch.network.extentions.onException
import com.uyoung.sportsmatch.network.extentions.onSuccess
import com.uyoung.sportsmatch.network.model.ApiResponse
import com.uyoung.sportsmatch.network.model.ApiResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class WriteRepository @Inject constructor(
    private val mapRemoteDataSource: MapRemoteDataSource,
    private val postRemoteDataSource: PostRemoteDataSource,
) {

    fun getMap(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        searchText: String
    ): Flow<ApiResponse<SearchPlaceList>> = flow {
        val key = BuildConfig.KAKAO_API_KEY
        val response = mapRemoteDataSource.getMap(key, searchText)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)


    fun addPost(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        title: String,
        category: String,
        type: String,
        date: String,
        markerPlace: MarkerPlace,
        content: String
    ): Flow<ApiResponse<Map<String, String>>?> = flow {
        val response =
            postRemoteDataSource.addPost(title, category, type, date, markerPlace, content)
        response?.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }?.onError { code, message ->
            onError("code: $code, message: $message")
        }?.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)
}