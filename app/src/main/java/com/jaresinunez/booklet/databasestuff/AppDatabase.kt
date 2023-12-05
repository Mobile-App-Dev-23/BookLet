package com.jaresinunez.booklet.databasestuff

import android.content.Context
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jaresinunez.booklet.databasestuff.entities.BookEntity
import com.jaresinunez.booklet.databasestuff.entities.CompletedBookEntity
import com.jaresinunez.booklet.databasestuff.entities.CurrentBookEntity
import com.jaresinunez.booklet.databasestuff.entities.FutureBookEntity

@Database(entities = [BookEntity::class, CurrentBookEntity::class, FutureBookEntity::class, CompletedBookEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDaos(): BookDAOs

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "Books-db"
            ).build()
    }
}