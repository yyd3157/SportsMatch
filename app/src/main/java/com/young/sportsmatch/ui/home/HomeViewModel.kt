package com.young.sportsmatch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.young.sportsmatch.data.model.Category
import com.young.sportsmatch.data.model.Post
import com.young.sportsmatch.data.source.BookmarkRepository
import com.young.sportsmatch.data.source.HomeRepository
import com.young.sportsmatch.network.model.ApiResultSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {

    private val _items = MutableStateFlow<Map<String, Post>?>(null)
    val items: StateFlow<Map<String, Post>?> = _items

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _bookmarkStatus = MutableStateFlow<MutableMap<String, Boolean>>(mutableMapOf())
    val bookmarkStatus: StateFlow<MutableMap<String, Boolean>> = _bookmarkStatus

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category.value.toString()
        getPostList(_selectedCategory.value!!)
    }

    fun getPostList(category: String) {
        viewModelScope.launch {
            homeRepository.getPostList(
                onComplete = { },
                onError = { },
            ).collect { response ->
                if (response is ApiResultSuccess) {
                    val posts = response.data
                    val filteredPosts = posts.filter {
                        val categoryValue = it.value.category
                        categoryValue == category
                    }
                    _items.value = filteredPosts.toMap()
                } else {
                    _items.value = null
                }
            }
        }
    }

    fun updateBookmarkPost(post: Post, category: String) {
        val latestBookmarkStatus = _bookmarkStatus.value ?: mutableMapOf()
        val isBookmarked = latestBookmarkStatus[post.hashCode().toString()] ?: false

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (isBookmarked) {
                    bookmarkRepository.removeBookmarkPost(post)
                } else {
                    bookmarkRepository.addBookmarkPost(post, category)
                }
            }
            latestBookmarkStatus[post.hashCode().toString()] = !isBookmarked
            _bookmarkStatus.value = latestBookmarkStatus
        }
    }

    fun loadBookmarkState() {
        viewModelScope.launch {
            val bookmarkList = bookmarkRepository.getPost() ?: emptyList()
            val bookmarkStatusMap = mutableMapOf<String, Boolean>()

            for (bookmark in bookmarkList) {
                bookmarkStatusMap[bookmark.post.hashCode().toString()] = true
            }

            for ((postId, _) in _items.value ?: emptyMap()) {
                bookmarkStatusMap.putIfAbsent(postId, false)
            }
            _bookmarkStatus.value = bookmarkStatusMap
        }
    }
}