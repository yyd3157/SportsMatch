package com.uyoung.sportsmatch.network.extentions

import com.uyoung.sportsmatch.network.model.ApiResponse
import com.uyoung.sportsmatch.network.model.ApiResultError
import com.uyoung.sportsmatch.network.model.ApiResultException
import com.uyoung.sportsmatch.network.model.ApiResultSuccess

suspend inline fun <T : Any> ApiResponse<T>.onSuccess(
    crossinline block: suspend (T) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResultSuccess<T>) {
        block(data)
    }
}

suspend inline fun <T : Any> ApiResponse<T>.onError(
    crossinline block: suspend (code: Int, message: String) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResultError<T>) {
        block(code, message)
    }
}

suspend inline fun <T : Any> ApiResponse<T>.onException(
    crossinline block: suspend (throwable: Throwable) -> Unit
): ApiResponse<T> = apply {
    if (this is ApiResultException<T>) {
        block(throwable)
    }
}