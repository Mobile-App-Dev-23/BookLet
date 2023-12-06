package com.jaresinunez.booklet.databasestuff.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "book_table")
data class BookEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "bookTitle") val title: String?,
    @ColumnInfo(name = "bookAuthor") val author: String?,
    @ColumnInfo(name = "bookDescription") val description: String?,
    @ColumnInfo(name = "bookRating") val rating: Double?,
    @ColumnInfo(name = "bookPageCount") val pageCount: Int?,
    @ColumnInfo(name = "bookCoverURL") val coverUrl: String?,
    @ColumnInfo(name = "bookPurchaseURL") val purchaseUrl: String?,
    @ColumnInfo(name = "current") val current: Boolean,
    @ColumnInfo(name = "completed") val completed: Boolean,
    @ColumnInfo(name = "future") val future: Boolean
)


