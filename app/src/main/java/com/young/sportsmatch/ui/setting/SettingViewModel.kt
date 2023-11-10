package com.young.sportsmatch.ui.setting

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.young.sportsmatch.data.model.User
import com.young.sportsmatch.data.source.SettingRepository
import com.young.sportsmatch.network.model.ApiResultError
import com.young.sportsmatch.network.model.ApiResultException
import com.young.sportsmatch.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepository,
) : ViewModel() {

    private var imageUrl: Uri? = null
    private val auth = FirebaseAuth.getInstance()
    private val _logout = MutableLiveData<Boolean>()
    val logout: LiveData<Boolean> = _logout
    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> = _userInfo

    fun addUser(nickname: String) {
        viewModelScope.launch {
            val response = repository.addUser(nickname, imageUrl.toString())
            when (response) {
                is ApiResultSuccess -> {
                    Log.d("ViewModel", "success: ${response.data}")
                }
                is ApiResultError -> {
                    Log.d("ViewModel", "error code: ${response.code}, message: ${response.message}")
                }
                is ApiResultException -> {
                    Log.d("ViewModel", "exception: ${response.throwable}")
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            when (response) {
                is ApiResultSuccess -> {
                    Log.d("ViewModel", "success: ${response.data}")
                    val user = response.data
                    _userInfo.postValue(user)
                }
                is ApiResultError -> {
                    Log.d("ViewModel", "error code: ${response.code}, message: ${response.message}")
                    _userInfo.postValue(null)
                }
                is ApiResultException -> {
                    Log.d("ViewModel", "exception: ${response.throwable}")
                    _userInfo.postValue(null)
                }
            }
        }
    }

    fun updateSelectedImage(uri: Uri) {
        imageUrl = uri
    }

    fun logout() {
        auth.signOut()
        _logout.postValue(true)
    }
}