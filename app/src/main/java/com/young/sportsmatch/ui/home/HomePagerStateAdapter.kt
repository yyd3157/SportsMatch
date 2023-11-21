package com.young.sportsmatch.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.young.sportsmatch.data.model.Category

class HomePagerStateAdapter(
    fragment: Fragment,
    private val category: List<Category>
) : FragmentStateAdapter(fragment) {


    override fun getItemCount(): Int {
        return category.size
    }

    override fun createFragment(position: Int): Fragment {
        val category = category[position]
        return CategoryPostListFragment.newInstance(category)
    }
}