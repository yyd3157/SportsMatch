package com.young.sportsmatch.data.source

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.young.sportsmatch.data.model.User
import com.young.sportsmatch.data.source.remote.UserRemoteDataSource
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

class SettingRepository @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
) {

    fun addUser(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        nickName: String,
        imageUrl: String?
    ): Flow<ApiResponse<Map<String, String>>> = flow {
        val imageUri = uploadImage(imageUrl?.toUri())
        val auth = FirebaseAuth.getInstance().currentUser
        val userId = auth?.uid
        val authToken = auth?.getIdToken(true)?.await()?.token
        val response = remoteDataSource.addUser(userId.toString(),
            authToken.toString(), User(nickName, imageUri))
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

    fun getUser(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
    ): Flow<ApiResponse<User>> = flow {
        val auth = FirebaseAuth.getInstance().currentUser
        val userId = auth?.uid
        val authToken = auth?.getIdToken(true)?.await()?.token
        val response = remoteDataSource.getUser(userId.toString(), authToken.toString())
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

    suspend fun uploadImage(image: Uri?): String {
        return remoteDataSource.uploadImage(image)
    }
}