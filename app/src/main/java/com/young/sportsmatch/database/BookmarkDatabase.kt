package com.young.sportsmatch.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BookmarkEntity::class], version = 1)
@TypeConverters(Converter::class)
abstract class BookmarkDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao

    companion object {
        private var INSTANCE: BookmarkDatabase? = null

        fun getDatabase(context: Context): BookmarkDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    BookmarkDatabase::class.java, "postDB"
                ).build()
            }
            return INSTANCE!!
        }
    }
}