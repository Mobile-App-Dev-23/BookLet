package com.jaresinunez.booklet

data class DisplayItem(
    val id: Long,
    val bookTitle: String?,
    val bookAuthor: String?,
    val bookDescription: String?,
    val bookRating: Double?,
    val bookPageCount: Int?,
    val bookCoverImage: ByteArray?,
    val bookPurchaseURL: String?,
    val current: Boolean,
    val completed: Boolean,
    val future: Boolean
): java.io.Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayItem

        if (id != other.id) return false
        if (bookTitle != other.bookTitle) return false
        if (bookAuthor != other.bookAuthor) return false
        if (bookDescription != other.bookDescription) return false
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
