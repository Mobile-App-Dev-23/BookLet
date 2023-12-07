package com.jaresinunez.booklet.databasestuff

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jaresinunez.booklet.databasestuff.entities.BookEntity
import com.jaresinunez.booklet.databasestuff.entities.CompletedBookEntity
import com.jaresinunez.booklet.databasestuff.entities.CurrentBookEntity
import com.jaresinunez.booklet.databasestuff.entities.FutureBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDAOs {
    // getAll()
    @Query("SELECT * FROM book_table WHERE current=1")
    fun getAllCurrents(): Flow<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE completed=1")
    fun getAllCompleted(): Flow<List<BookEntity>>

    @Query("SELECT * FROM book_table WHERE future=1")
    fun getAllFuture(): Flow<List<BookEntity>>

    @Query("SELECT bookCoverImage FROM book_table WHERE id = :bookId")
    fun getBookCoverImageById(bookId: Long): ByteArray?

    // insert()
    @Insert
    fun insertBook(book: BookEntity)

    @Insert
    fun insertAllCurrents(books: CurrentBookEntity)

    @Insert
    fun insertAllFuture(books: FutureBookEntity)

    @Insert
    fun insertAllCompleted(books: CompletedBookEntity)

    // delete()
    @Query("DELETE FROM book_table WHERE current=1")
    fun deleteAllCurrents()

    @Query("DELETE FROM book_table WHERE future=1")
    fun deleteAllFuture()

    @Query("DELETE FROM book_table WHERE completed=1")
    fun deleteAllCompleted()

    /*
    @Query("SELECT AVG(calories) as average FROM food_item_table")
    fun getAverageCals(): Flow<Int>

    @Query("SELECT MIN(calories) as minimum FROM food_item_table")
    fun getMinCals(): Flow<Int>

    @Query("SELECT MAX(calories) as maximum FROM food_item_table")
    fun getMaxCals(): Flow<Int>
     */
}