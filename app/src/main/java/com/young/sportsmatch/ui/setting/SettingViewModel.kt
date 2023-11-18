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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val repository: SettingRepository,
) : ViewModel() {

    private var imageUrl: Uri? = null
    private val auth = FirebaseAuth.getInstance()
    private val _logout = MutableStateFlow<Boolean>(false)
    val logout: StateFlow<Boolean> = _logout
    private val _userInfo = MutableStateFlow<User?>(null)
    val userInfo: StateFlow<User?> = _userInfo

    fun addUser(nickname: String) {
        viewModelScope.launch {
            repository.addUser(
                onComplete = { },
                onError = { },
                nickname,
                imageUrl.toString(),
                ). collect { }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            repository.getUser(
                onComplete = { },
                onError = { },
            ).collect { response ->
                if (response is ApiResultSuccess) {
                    _userInfo.value = response.data
                } else {
                    _userInfo.value = null
                }
            }
        }
    }

    fun updateSelectedImage(uri: Uri) {
        imageUrl = uri
    }

    fun logout() {
        auth.signOut()
        _logout.value = true
    }
}