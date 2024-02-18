package com.uyoung.sportsmatch.network.di

import com.uyoung.sportsmatch.database.BookmarkDao
import com.uyoung.sportsmatch.database.BookmarkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BookmarkDaoModule {

    @Provides
    fun provideBookmarkDao(database: BookmarkDatabase): BookmarkDao {
        return database.bookmarkDao()
    }
}