package com.young.sportsmatch.ui.setting

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.young.sportsmatch.data.model.User
import com.young.sportsmatch.data.source.SettingRepository
import kotlinx.coroutines.launch


class SettingViewModel(
    private val repository: SettingRepository,
) : ViewModel() {

    private var imageUrl: Uri? = null
    private val _userInfo = MutableLiveData<User?>()
    val userInfo: LiveData<User?> = _userInfo

    fun addUser(nickname: String) {
        viewModelScope.launch {
            val response = repository.addUser(nickname, imageUrl.toString())
            if (response.isSuccessful) {
                getUser()
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            val response = repository.getUser()
            Log.d("post", "$response")
            if (response.isSuccessful) {
                val user = response.body()
                _userInfo.postValue(user)
            } else {
                _userInfo.postValue(null)
            }
        }
    }

    fun updateSelectedImage(uri: Uri) {
        imageUrl = uri
    }

    companion object {

        fun provideFactory(repository: SettingRepository) = viewModelFactory {
            initializer {
                SettingViewModel(repository)
            }
        }
    }
}