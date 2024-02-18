package com.uyoung.sportsmatch.ui.bookmark

import com.uyoung.sportsmatch.data.model.BookMarkItem
import com.uyoung.sportsmatch.data.model.Category
import com.uyoung.sportsmatch.database.BookmarkEntity

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
        val categoryItems = savedArticleList.filter { it.category == category }
        val reversedCategoryItems = categoryItems.reversed()

        val savedArticleSection = BookMarkItem.BookMarkSection(
            reversedCategoryItems,
            category
        )

        if (savedArticleSection.bookMarkList.isNotEmpty()) {
            return savedArticleSection
        }
        return null
    }
}
