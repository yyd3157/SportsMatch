package com.young.sportsmatch.data.source.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.young.sportsmatch.data.model.User
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class UserRemoteDataSource(private val apiClient: ApiClient) {

    suspend fun addUser(userId: String, auth: String, user: User): Response<Map<String, String>> {
        return apiClient.addUser(userId, auth, user)
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