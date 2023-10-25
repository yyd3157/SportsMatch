package com.young.sportsmatch.data.source

import android.net.Uri
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.young.sportsmatch.data.model.User
import com.young.sportsmatch.data.source.remote.UserRemoteDataSource
import kotlinx.coroutines.tasks.await
import retrofit2.Response

class SettingRepository(
    private val remoteDataSource: UserRemoteDataSource,
) {

    suspend fun addUser(nickName: String, imageUrl: String): Response<Map<String, String>> {
        val imageUri = uploadImage(imageUrl.toUri())
        val auth = FirebaseAuth.getInstance().currentUser
        val userId = auth?.uid
        val authToken = auth?.getIdToken(true)?.await()?.token
        return remoteDataSource.addUser(userId.toString(),
            authToken.toString(), User(nickName, imageUri))
    }

    private suspend fun uploadImage(image: Uri?): String {
        return remoteDataSource.uploadImage(image)
    }
}