package com.young.sportsmatch.network

import com.young.sportsmatch.data.model.User
import com.young.sportsmatch.network.model.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
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

}