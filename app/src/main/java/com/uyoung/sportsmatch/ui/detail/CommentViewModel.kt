package com.uyoung.sportsmatch.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uyoung.sportsmatch.data.source.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: CommentRepository,
) : ViewModel() {

    fun addComment(
        postId: String,
        commentContent: String,
        timeStamp: String,
    ) {
        viewModelScope.launch {
            repository.addComment(
                onComplete = { },
                onError = { },
                postId,
                commentContent,
                timeStamp,
            ).collect { }
        }
    }

}