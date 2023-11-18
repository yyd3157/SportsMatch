package com.young.sportsmatch.ui.write

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.young.sportsmatch.data.model.MarkerPlace
import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.data.source.WriteRepository
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
class WriteViewModel @Inject constructor(
    private val repository: WriteRepository,
) : ViewModel() {

    private val _searchMap = MutableStateFlow<SearchPlaceList?>(null)
    val searchMap: StateFlow<SearchPlaceList?> = _searchMap

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
            repository.addPost(
                onComplete = { },
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
