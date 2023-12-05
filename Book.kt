package com.example.booklet

data class Book(
    var title: String,
    var author: String,
    var page: Int,
    var notes: String,
    var coverUrl: String? = null, // For the optional cover image
    var purchaseUrl: String? = null // For the optional purchase link
)
