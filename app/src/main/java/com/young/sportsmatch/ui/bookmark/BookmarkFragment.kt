package com.young.sportsmatch.ui.bookmark

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.young.sportsmatch.database.BookmarkDao
import com.young.sportsmatch.database.BookmarkDatabase
import com.young.sportsmatch.databinding.FragmentBookmarkBinding
import com.young.sportsmatch.ui.home.HomeViewModel
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
        bookmarkDao = BookmarkDatabase.getDatabase(requireContext()).bookmarkDao()
    }

    override fun onResume() {
        super.onResume()
        setLayout()
    }

    private fun setLayout() {
        adapter = BookmarkListAdapter({ _, _ ->
            // 클릭 이벤트 처리
        }, viewModel)
        binding.rvSaved.adapter = adapter

        lifecycleScope.launch {
            val response =  SavedArticleDataManager.getSavedDataList(bookmarkDao.getAll()!!)
            Log.d("bookmark123","$response")
            adapter.submitList(response)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}