package com.young.sportsmatch.data.source.remote

import com.google.firebase.auth.FirebaseAuth
import com.young.sportsmatch.data.model.MarkerPlace
import com.young.sportsmatch.data.model.Post
import com.young.sportsmatch.data.model.User
import com.young.sportsmatch.network.ApiClient
import com.young.sportsmatch.network.model.ApiResponse
import com.young.sportsmatch.network.model.ApiResultSuccess
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PostRemoteDataSource @Inject constructor(private val apiClient: ApiClient) {

    suspend fun addPost(title: String, category: String, type: String, date: String, markerPlace: MarkerPlace, content: String): ApiResponse<Map<String, String>>? {
        val auth = FirebaseAuth.getInstance().currentUser
        val userId = auth?.uid
        val authToken = auth?.getIdToken(true)?.await()?.token
        val userResponse = getUser(userId.toString(), authToken.toString())
        if (userResponse is ApiResultSuccess) {
            val user = userResponse.data
            val post = Post(user, title, category, type, date, markerPlace, content)
            return apiClient.addPost(authToken!!, post)
        } else {
            return null
        }
    }

    suspend fun getUser(userId: String, auth: String): ApiResponse<User> {
        return apiClient.getUser(userId, auth)
    }

    suspend fun getPostList(auth: String): ApiResponse<Map<String, Post>> {
        return apiClient.getPostList(auth)
    }
}