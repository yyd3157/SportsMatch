package com.uyoung.sportsmatch.network

import com.uyoung.sportsmatch.data.model.Post
import com.uyoung.sportsmatch.data.model.User
import com.uyoung.sportsmatch.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiClient {

    @GET("user/{userId}.json")
    suspend fun getUser(
        @Path("userId") userId: String,
        @Query("auth") auth: String,
    ): ApiResponse<User>

    @PUT("user/{userId}.json")
    suspend fun addUser(
        @Path("userId") userId: String,
        @Query("auth") auth: String,
        @Body user: User,
    ): ApiResponse<Map<String, String>>

    @POST("post.json")
    suspend fun addPost(
        @Query("auth") auth: String,
        @Body post: Post,
    ): ApiResponse<Map<String, String>>

    @GET("post.json")
    suspend fun getPostList(
        @Query("auth") auth: String,
    ): ApiResponse<Map<String, Post>>
}