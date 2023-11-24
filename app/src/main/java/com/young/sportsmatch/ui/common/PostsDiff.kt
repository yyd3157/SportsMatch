package com.young.sportsmatch.ui.common

import androidx.recyclerview.widget.DiffUtil
import com.young.sportsmatch.data.model.Post

class PostsDiff : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}