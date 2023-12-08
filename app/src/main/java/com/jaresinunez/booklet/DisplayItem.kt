package com.jaresinunez.booklet

import com.jaresinunez.booklet.databasestuff.entities.BookEntity

data class DisplayItem(
    val id: Long,
    val bookTitle: String?,
    val bookAuthor: String?,
    val bookDescription: String?,
    val bookReview: String?,
    val bookRating: Double?,
    val bookPageCount: Int?,
    val bookCoverImage: ByteArray?,
    val bookPurchaseURL: String?,
    val current: Boolean,
    val completed: Boolean,
    val future: Boolean
): java.io.Serializable {
    fun toBookEntity(): BookEntity {
        return BookEntity(
            id = this.id,
            title = this.bookTitle.orEmpty(),
            author = this.bookAuthor.orEmpty(),
            description = this.bookDescription.orEmpty(),
            review = this.bookReview.orEmpty(),
            rating = this.bookRating ?: 0.0, // Provide a default value if bookRating is null
            pageCount = this.bookPageCount ?: 0, // Provide a default value if pageCount is null
            coverImage = this.bookCoverImage ?: ByteArray(0), // Provide a default value if coverImage is null
            purchaseUrl = this.bookPurchaseURL.orEmpty(),
            current = this.current,
            completed = this.completed,
            future = this.future
        )
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayItem

        if (id != other.id) return false
        if (bookTitle != other.bookTitle) return false
        if (bookAuthor != other.bookAuthor) return false
        if (bookDescription != other.bookDescription) return false
        if (bookReview != other.bookReview) return false
        if (bookRating != other.bookRating) return false
        if (bookPageCount != other.bookPageCount) return false
        if (bookCoverImage != null) {
            if (other.bookCoverImage == null) return false
            if (!bookCoverImage.contentEquals(other.bookCoverImage)) return false
        } else if (other.bookCoverImage != null) return false
        if (bookPurchaseURL != other.bookPurchaseURL) return false
        if (current != other.current) return false
        if (completed != other.completed) return false
        if (future != other.future) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (bookTitle?.hashCode() ?: 0)
        result = 31 * result + (bookAuthor?.hashCode() ?: 0)
        result = 31 * result + (bookDescription?.hashCode() ?: 0)
        result = 31 * result + (bookReview?.hashCode() ?: 0)
        result = 31 * result + (bookRating?.hashCode() ?: 0)
        result = 31 * result + (bookPageCount ?: 0)
        result = 31 * result + (bookCoverImage?.contentHashCode() ?: 0)
        result = 31 * result + (bookPurchaseURL?.hashCode() ?: 0)
        result = 31 * result + current.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + future.hashCode()
        return result
    }
}
