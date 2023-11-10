package com.young.sportsmatch.data.source.remote

import com.young.sportsmatch.data.model.User
import retrofit2.Response
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
    ): Response<User>

    @PUT("user/{userId}.json")
    suspend fun addUser(
        @Path("userId") userId: String,
        @Query("auth") auth: String,
        @Body user: User,
    ): Response<Map<String, String>>

}