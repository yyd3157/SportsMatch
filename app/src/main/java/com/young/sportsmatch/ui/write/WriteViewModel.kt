package com.young.sportsmatch.ui.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.young.sportsmatch.data.model.MarkerPlace
import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.data.source.WriteRepository
import com.young.sportsmatch.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val repository: WriteRepository,
) : ViewModel() {

    private val _searchMap = MutableStateFlow<SearchPlaceList?>(null)
    val searchMap: StateFlow<SearchPlaceList?> = _searchMap
    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun getMap(searchText: String) {
        viewModelScope.launch {
            repository.getMap(
                onComplete = { },
                onError = { _searchMap.value = null },
                searchText,
            ).collect { response ->
                if (response is ApiResultSuccess) {
                    _searchMap.value = response.data
                } else {
                    _searchMap.value = null
                }
            }
        }
    }
    fun addPost(
        title: String,
        category: String,
        type: String,
        date: String,
        markerPlace: MarkerPlace,
        content: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.addPost(
                onComplete = { _isLoading.value = false },
                onError = { },
                title,
                category,
                type,
                date,
                markerPlace,
                content
            ).collect { }
        }
    }
}
