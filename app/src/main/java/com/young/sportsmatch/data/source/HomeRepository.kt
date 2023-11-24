package com.young.sportsmatch.data.source

import com.google.firebase.auth.FirebaseAuth
import com.young.sportsmatch.data.model.Post
import com.young.sportsmatch.data.source.remote.PostRemoteDataSource
import com.young.sportsmatch.network.extentions.onError
import com.young.sportsmatch.network.extentions.onException
import com.young.sportsmatch.network.extentions.onSuccess
import com.young.sportsmatch.network.model.ApiResponse
import com.young.sportsmatch.network.model.ApiResultSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepository @Inject constructor(
    private val postRemoteDataSource: PostRemoteDataSource,
) {

    fun getPostList(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
    ): Flow<ApiResponse<Map<String, Post>>> = flow {
        val auth = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
        val response = postRemoteDataSource.getPostList(auth.toString())
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

}