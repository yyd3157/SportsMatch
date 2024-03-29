package com.uyoung.sportsmatch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.uyoung.sportsmatch.R
import com.uyoung.sportsmatch.data.model.Category
import com.uyoung.sportsmatch.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeCategories = Category.values().toList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideActivityMenu()
        val adapter = HomePagerStateAdapter(this, homeCategories)
        binding.homePager.adapter = adapter

        TabLayoutMediator(binding.homeTab, binding.homePager) { tab, position ->
            tab.text = homeCategories[position].toString()
        }.attach()
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
