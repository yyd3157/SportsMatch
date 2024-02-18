package com.uyoung.sportsmatch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uyoung.sportsmatch.data.model.Category
import com.uyoung.sportsmatch.data.model.Constants
import com.uyoung.sportsmatch.data.model.Post
import com.uyoung.sportsmatch.databinding.FragmentHomeCategoryBinding
import com.uyoung.sportsmatch.ui.detail.DetailFragmentDirections
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
                navigateToDetail(post)
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

    private fun navigateToDetail(post: Post) {
        val action = DetailFragmentDirections.actionGlobalDetail(post)
        findNavController().navigate(action)
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
