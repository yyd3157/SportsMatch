package com.uyoung.sportsmatch.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.uyoung.sportsmatch.data.model.Comment
import com.uyoung.sportsmatch.data.model.User
import com.uyoung.sportsmatch.network.ApiClient
import com.uyoung.sportsmatch.network.model.ApiResponse
import com.uyoung.sportsmatch.network.model.ApiResultSuccess
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentRemoteDataSource @Inject constructor(private val apiClient: ApiClient) {

    suspend fun addComment(postId: String, commentContent: String, timeStamp: String): ApiResponse<Map<String, String>>? {
        val auth = FirebaseAuth.getInstance().currentUser
        val userId = auth?.uid
        val authToken = auth?.getIdToken(true)?.await()?.token
        val userResponse = getUser(userId.toString(), authToken.toString())
        if (userResponse is ApiResultSuccess) {
            val user = userResponse.data
            val comment = Comment(postId, user.nickName, commentContent, timeStamp)
            return apiClient.addComment(authToken!!, comment)
        } else {
            return null
        }
    }

    suspend fun getUser(userId: String, auth: String): ApiResponse<User> {
        return apiClient.getUser(userId, auth)
    }
}