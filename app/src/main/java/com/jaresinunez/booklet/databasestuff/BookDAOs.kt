package com.jaresinunez.booklet.databasestuff

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT COUNT(*) FROM book_table")
    fun getBookTableSize(): Flow<Int>

    // insert()
    @Insert
    fun insertBook(book: BookEntity):Long

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

    @Query("DELETE FROM book_table WHERE id = :bookId")
    fun deleteBook(bookId: Long)

    //update
    @Update
    fun updateBook(book: BookEntity)
}