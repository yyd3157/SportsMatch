package com.young.sportsmatch.network.di

import android.content.Context
import com.young.sportsmatch.database.BookmarkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object BookmarkDatabaseModule {

    @Provides
    fun provideBookmarkDatabase(@ApplicationContext context: Context): BookmarkDatabase {
        return BookmarkDatabase.getDatabase(context)
    }
}