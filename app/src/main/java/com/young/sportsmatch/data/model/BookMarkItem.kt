package com.young.sportsmatch.data.model

import com.young.sportsmatch.database.BookmarkEntity

sealed class BookMarkItem {
    data class BookMarkSection(
        val bookMarkList: List<BookmarkEntity>,
        override val id: String
    ) : BookMarkItem()

    abstract val id: String
}