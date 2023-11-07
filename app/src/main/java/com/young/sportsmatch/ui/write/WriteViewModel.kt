package com.young.sportsmatch.ui.write

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.young.sportsmatch.data.model.SearchPlaceList
import com.young.sportsmatch.data.source.WriteRepository
import com.young.sportsmatch.network.model.ApiResultError
import com.young.sportsmatch.network.model.ApiResultException
import com.young.sportsmatch.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val repository: WriteRepository,
) : ViewModel() {

    private val _searchMap = MutableLiveData<SearchPlaceList?>()
    val searchMap: LiveData<SearchPlaceList?> = _searchMap

    fun getMap(searchText: String) {
        viewModelScope.launch {
                val response = repository.getMap(searchText)
                when (response) {
                    is ApiResultSuccess -> {
                        Log.d("ViewModel", "success: ${response.data}")
                        _searchMap.postValue(response.data)
                    }
                    is ApiResultError -> {
                        Log.d("ViewModel", "Response error: ${response.code}, message: ${response.message}")
                        _searchMap.postValue(null)
                    }
                    is ApiResultException -> {
                        Log.d("ViewModel", "네트워크 요청 실패: ${response.throwable.message}")
                        _searchMap.postValue(null)
                    }
                }
        }
    }
}
