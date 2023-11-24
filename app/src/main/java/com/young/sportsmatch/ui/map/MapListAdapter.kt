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

class MapListAdapter(private val onItemClick: (Post) -> Unit) : ListAdapter<Post, MapListAdapter.ViewHolder>(
    PostsDiff()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(private val binding: ItemPostMapBinding) :
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
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemPostMapBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ViewHolder(binding)
            }
        }
    }
}