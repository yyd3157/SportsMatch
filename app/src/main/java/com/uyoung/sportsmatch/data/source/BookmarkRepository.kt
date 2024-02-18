package com.uyoung.sportsmatch.data.source

import com.uyoung.sportsmatch.data.model.Post
import com.uyoung.sportsmatch.database.BookmarkDatabase
import com.uyoung.sportsmatch.database.BookmarkEntity
import javax.inject.Inject

class BookmarkRepository @Inject constructor(
    database: BookmarkDatabase,
    ) {

    private val dao = database.bookmarkDao()

    suspend fun addBookmarkPost(post: Post, category: String?) {
        val title = post.title
        val bookmarkPost = dao.getAll()?.find { it.post.title == title }
        if (bookmarkPost == null) {
            val bookmarkList = BookmarkEntity(post = post, category = category)
            dao.insertAll(bookmarkList)
        }
    }

    suspend fun removeBookmarkPost(post: Post) {
        val title = post.title
        val bookmarkPost = dao.getAll()?.find { it.post.title == title }

        bookmarkPost?.let {
            dao.delete(it)
        }
    }

    suspend fun getPost(): List<BookmarkEntity>? {
        return dao.getAll()
    }
}