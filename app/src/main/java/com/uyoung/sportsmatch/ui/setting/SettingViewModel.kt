package com.uyoung.sportsmatch.ui.setting

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.uyoung.sportsmatch.data.model.User
import com.uyoung.sportsmatch.data.source.SettingRepository
import com.uyoung.sportsmatch.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun addUser(nickname: String) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addUser(
                onComplete = { _isLoading.value = false },
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