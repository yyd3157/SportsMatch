package com.uyoung.sportsmatch.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uyoung.sportsmatch.R
import com.uyoung.sportsmatch.data.model.Post
import com.uyoung.sportsmatch.database.BookmarkDao
import com.uyoung.sportsmatch.database.BookmarkDatabase
import com.uyoung.sportsmatch.databinding.FragmentBookmarkBinding
import com.uyoung.sportsmatch.ui.detail.DetailFragmentDirections
import com.uyoung.sportsmatch.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookmarkDao: BookmarkDao
    private lateinit var adapter: BookmarkListAdapter
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActivityMenu()
        bookmarkDao = BookmarkDatabase.getDatabase(requireContext()).bookmarkDao()
    }

    override fun onResume() {
        setLayout()
        super.onResume()
    }

    private fun setLayout() {
        adapter = BookmarkListAdapter({ posts ->
            val post = posts!!.post
            navigateToDetail(post)
        }, viewModel)
        binding.rvSaved.adapter = adapter

        lifecycleScope.launch {
            val response =  SavedArticleDataManager.getSavedDataList(bookmarkDao.getAll()!!)
            Log.d("bookmark123","$response")
            adapter.submitList(response)
        }
    }

    private fun navigateToDetail(post: Post) {
        val action = DetailFragmentDirections.actionGlobalDetail(post)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideActivityMenu() {
        val backButton = activity?.findViewById<ImageView>(R.id.iv_back)
        backButton?.visibility = View.GONE
    }
}