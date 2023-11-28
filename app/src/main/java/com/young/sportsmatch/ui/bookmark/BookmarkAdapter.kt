package com.young.sportsmatch.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.young.sportsmatch.R
import com.young.sportsmatch.database.BookmarkEntity
import com.young.sportsmatch.databinding.ItemPostBookmarkBinding
import com.young.sportsmatch.ui.home.HomeViewModel

class BookmarkAdapter(
    private val viewModel: HomeViewModel,
    private val onItemClick: (BookmarkEntity?) -> Unit
) : ListAdapter<BookmarkEntity, BookmarkAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, viewModel, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemPostBookmarkBinding,
        private val viewModel: HomeViewModel,
        private val onItemClick: (BookmarkEntity?) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmarkEntity: BookmarkEntity) {
            binding.postList = bookmarkEntity.post
            binding.viewModel = viewModel
            val imageUrl = bookmarkEntity.post.user.imageUrl
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
                onItemClick(bookmarkEntity)
            }
        }

        companion object {
            fun from(
                parent: ViewGroup,
                viewModel: HomeViewModel,
                onItemClick: (BookmarkEntity?) -> Unit
            ): ViewHolder {
                val binding = ItemPostBookmarkBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding, viewModel, onItemClick)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<BookmarkEntity>() {
        override fun areItemsTheSame(oldItem: BookmarkEntity, newItem: BookmarkEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BookmarkEntity, newItem: BookmarkEntity): Boolean {
            return oldItem == newItem
        }
    }
}