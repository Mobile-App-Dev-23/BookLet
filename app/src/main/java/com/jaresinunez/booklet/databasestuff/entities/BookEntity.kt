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
    @ColumnInfo(name = "bookReview") val review: String?,
    @ColumnInfo(name = "bookRating") val rating: Double?,
    @ColumnInfo(name = "bookPageCount") val pageCount: Int?,
    @ColumnInfo(name = "bookCoverImage") val coverImage: ByteArray?,
    @ColumnInfo(name = "bookPurchaseURL") val purchaseUrl: String?,
    @ColumnInfo(name = "current") val current: Boolean,
    @ColumnInfo(name = "completed") val completed: Boolean,
    @ColumnInfo(name = "future") val future: Boolean
) {
    override fun toString(): String{
        return "$title by $author"
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookEntity

        if (id != other.id) return false
        if (title != other.title) return false
        if (author != other.author) return false
        if (description != other.description) return false
        if (review != other.review) return false
        if (rating != other.rating) return false
        if (pageCount != other.pageCount) return false
        if (coverImage != null) {
            if (other.coverImage == null) return false
            if (!coverImage.contentEquals(other.coverImage)) return false
        } else if (other.coverImage != null) return false
        if (purchaseUrl != other.purchaseUrl) return false
        if (current != other.current) return false
        if (completed != other.completed) return false
        if (future != other.future) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (author?.hashCode() ?: 0)
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (review?.hashCode() ?: 0)
        result = 31 * result + (rating?.hashCode() ?: 0)
        result = 31 * result + (pageCount ?: 0)
        result = 31 * result + (coverImage?.contentHashCode() ?: 0)
        result = 31 * result + (purchaseUrl?.hashCode() ?: 0)
        result = 31 * result + current.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + future.hashCode()
        return result
    }
}


