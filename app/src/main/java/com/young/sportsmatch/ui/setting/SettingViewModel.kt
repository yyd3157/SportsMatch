package com.young.sportsmatch.ui.setting

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.young.sportsmatch.data.source.SettingRepository
import kotlinx.coroutines.launch


class SettingViewModel(
    private val repository: SettingRepository,
) : ViewModel() {

    private var imageUrl: Uri? = null

    fun addUser(nickname: String) {
        viewModelScope.launch {
            repository.addUser(nickname, imageUrl.toString())
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