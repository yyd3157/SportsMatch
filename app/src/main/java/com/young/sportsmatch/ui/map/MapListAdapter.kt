package com.young.sportsmatch.ui.map

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
import com.young.sportsmatch.databinding.ItemPostMapBinding
import com.young.sportsmatch.ui.common.PostsDiff
import com.young.sportsmatch.ui.home.HomeViewModel

class MapListAdapter(private val onItemClick: (Post) -> Unit, private val viewModel: HomeViewModel) : ListAdapter<Post, MapListAdapter.ViewHolder>(
    PostsDiff()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(private val binding: ItemPostMapBinding, private val viewModel: HomeViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, onItemClick: (Post) -> Unit) {
            binding.postList = post
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
            binding.ivHomeFavorites.isSelected = isBookmarked
        }

        companion object {
            fun from(parent: ViewGroup, viewModel: HomeViewModel): ViewHolder {
                val binding = ItemPostMapBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding, viewModel)
            }
        }
    }
}