package com.jaresinunez.booklet

data class DisplayItem(
    val bookTitle: String,
    val bookAuthor: String,
    val bookDescription: String,
    val bookRating: Double,
    val bookPageCount: Int,
    val bookCoverURL: String?,
    val bookPurchaseURL: String?,
    val current: Boolean,
    val completed: Boolean,
    val future: Boolean
): java.io.Serializable
