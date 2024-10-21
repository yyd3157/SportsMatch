package com.uyoung.sportsmatch.data.source

import com.uyoung.sportsmatch.data.source.remote.CommentRemoteDataSource
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

class CommentRepository @Inject constructor(
    private val commentRemoteDataSource: CommentRemoteDataSource,
) {
    fun addComment(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postId: String,
        commentContent: String,
        timeStamp: String,
    ): Flow<ApiResponse<Map<String, String>>?> = flow {
        val response =
            commentRemoteDataSource.addComment(postId, commentContent, timeStamp)
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