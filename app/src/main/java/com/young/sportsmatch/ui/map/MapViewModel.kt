package com.young.sportsmatch.ui.map

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.young.sportsmatch.data.model.Category
import com.young.sportsmatch.data.model.Post
import com.young.sportsmatch.data.source.HomeRepository
import com.young.sportsmatch.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {

    private val _items = MutableStateFlow<Map<String, Post>?>(null)
    val items: StateFlow<Map<String, Post>?> = _items

    fun getPostList() {
        viewModelScope.launch {
            homeRepository.getPostList(
                onComplete = { },
                onError = { },
            ).collect { response ->
                if (response is ApiResultSuccess) {
                    _items.value = response.data
                } else {
                    _items.value = null
                }
            }
        }
    }
}
