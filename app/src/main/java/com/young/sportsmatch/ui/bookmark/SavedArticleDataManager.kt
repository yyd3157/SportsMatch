package com.young.sportsmatch.ui.bookmark

import android.util.Log
import com.young.sportsmatch.data.model.BookMarkItem
import com.young.sportsmatch.data.model.Category
import com.young.sportsmatch.database.BookmarkEntity

object SavedArticleDataManager {
    fun getSavedDataList(savedArticleList: List<BookmarkEntity>): List<BookMarkItem> {
        val savedDataList: MutableList<BookMarkItem> = mutableListOf()
        val processedCategories = mutableSetOf<String>()
        Category.values().forEach { category ->
            if (savedArticleList.any { it.category == category.value }) {
                if (!processedCategories.contains(category.value)) {
                    getSavedArticleSection(savedArticleList, category.value!!)?.let {
                        savedDataList.add(it)
                        processedCategories.add(category.value)
                    }
                }
            }
        }

        return savedDataList
    }

    private fun getSavedArticleSection(
        savedArticleList: List<BookmarkEntity>,
        category: String
    ): BookMarkItem.BookMarkSection? {
        val savedArticleSection = BookMarkItem.BookMarkSection(
            savedArticleList.filter { it.category == category },
            category
        )

        if (savedArticleSection.bookMarkList.isNotEmpty()) {
            return savedArticleSection
        }
        return null
    }
}