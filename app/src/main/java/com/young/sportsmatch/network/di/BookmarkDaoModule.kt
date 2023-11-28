package com.young.sportsmatch.network.di

import android.app.Application
import com.young.sportsmatch.database.BookmarkDao
import com.young.sportsmatch.database.BookmarkDatabase
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