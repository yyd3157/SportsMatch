package com.young.sportsmatch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.young.sportsmatch.data.model.Category
import com.young.sportsmatch.data.model.Constants
import com.young.sportsmatch.databinding.FragmentHomeCategoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryPostListFragment : Fragment() {

    private var _binding: FragmentHomeCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLayout() {
        viewModel.loadBookmarkState()
        val category = arguments?.getSerializable(Constants.ARG_CATEGORY) as? Category
        if (category != null) {
            val adapter = PostListAdapter({ post ->
                // 클릭 이벤트 처리
            }, viewModel)
            binding.rvPostList.adapter = adapter
            viewModel.onCategorySelected(category)
            lifecycleScope.launch {
                viewModel.items.flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                    .collect { posts ->
                        val selectedCategory = viewModel.selectedCategory.value
                        val filteredPosts = posts?.filter { it.value.category == selectedCategory }?.values?.toList()
                        adapter.submitList(filteredPosts)
                    }
            }
        }
    }

    companion object {
        fun newInstance(category: Category): CategoryPostListFragment {
            return CategoryPostListFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(Constants.ARG_CATEGORY, category)
                }
            }
        }
    }
}
