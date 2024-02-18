package com.uyoung.sportsmatch.data.source.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.uyoung.sportsmatch.data.model.User
import com.uyoung.sportsmatch.network.ApiClient
import com.uyoung.sportsmatch.network.model.ApiResponse
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(private val apiClient: ApiClient) {

    suspend fun addUser(userId: String, auth: String, user: User): ApiResponse<Map<String, String>> {
        return apiClient.addUser(userId, auth, user)
    }

    suspend fun getUser(userId: String, auth: String): ApiResponse<User> {
        return apiClient.getUser(userId, auth)
    }

    suspend fun uploadImage(image: Uri?): String {
        val storageRef = FirebaseStorage.getInstance().reference
        val name = FirebaseAuth.getInstance().currentUser?.displayName
        val location = "image/${name}_${System.currentTimeMillis()}"
        val imageRef = storageRef.child(location)
        if (image != null) {
            imageRef.putFile(image).await()
        }
        return location
    }
}