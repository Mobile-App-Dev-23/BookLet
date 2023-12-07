package com.example.booklet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Extension function to set up the RecyclerView for future books
fun RecyclerView.setupFutureBooks(
    books: MutableList<Book>,
    context: Context,
    onBookClick: (Book) -> Unit
) {
    this.adapter = FutureBooksAdapter(books, onBookClick)
    this.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
}

class FutureBooksAdapter(
    private val books: MutableList<Book>,
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<FutureBooksAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(book: Book, onBookClick: (Book) -> Unit) {
            itemView.findViewById<TextView>(R.id.titleTextView).text = book.title
            itemView.findViewById<TextView>(R.id.authorTextView).text = book.author
            // Set an onClickListener to handle book item clicks
            itemView.setOnClickListener { onBookClick(book) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position], onBookClick)
    }

    override fun getItemCount() = books.size
}
