package com.young.sportsmatch.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.young.sportsmatch.R
import com.young.sportsmatch.data.model.Post
import com.young.sportsmatch.databinding.ItemPostHomeBinding
import com.young.sportsmatch.ui.common.PostsDiff

class PostListAdapter(private val onItemClick: (Post) -> Unit, private val viewModel: HomeViewModel) : ListAdapter<Post, PostListAdapter.ViewHolder>(PostsDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(private val binding: ItemPostHomeBinding, private val viewModel: HomeViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, onItemClick: (Post) -> Unit) {
            binding.postList = post
            binding.viewModel = viewModel
            val imageUrl = post.user.imageUrl
            val storageReference: StorageReference = FirebaseStorage.getInstance().reference
            val imageRef = storageReference.child(imageUrl)
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                binding.ivProfileImage.load(uri) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_default_picture)
                    error(R.drawable.ic_default_picture)
                }
            }.addOnFailureListener { }
            itemView.setOnClickListener {
                onItemClick(post)
            }
            updateBookmarkButtonIcon(post.hashCode().toString())
        }

        fun updateBookmarkButtonIcon(postId: String) {
            val bookmarkStatus = viewModel.bookmarkStatus.value
            val isBookmarked = bookmarkStatus?.get(postId) ?: false
            val iconResId = if (isBookmarked) {
                R.drawable.ic_selected_bookmark
            } else {
                R.drawable.ic_bookmark
            }
            binding.ivHomeFavorites.setImageResource(iconResId)
        }

        companion object {
            fun from(parent: ViewGroup, viewModel: HomeViewModel): ViewHolder {
                val binding = ItemPostHomeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding, viewModel)
            }
        }
    }
}