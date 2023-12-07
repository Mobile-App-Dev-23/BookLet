package com.example.booklet

import EditBookActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var currentBooksAdapter: BooksAdapter
    private lateinit var futureBooksAdapter: BooksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the RecyclerView and its adapter for current books
        val currentBooksRecyclerView = findViewById<RecyclerView>(R.id.currentBooksRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        currentBooksAdapter = BooksAdapter { book ->
            // Handle click on book to edit
            val intent = Intent(this@MainActivity, EditBookActivity::class.java)
            intent.putExtra(EditBookActivity.EXTRA_BOOK, book)
            startActivity(intent)
        }
        currentBooksRecyclerView.adapter = currentBooksAdapter

        // Initialize the RecyclerView and its adapter for future books
        val futureBooksRecyclerView = findViewById<RecyclerView>(R.id.futureBooksRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        futureBooksAdapter = BooksAdapter()
        futureBooksRecyclerView.adapter = futureBooksAdapter

        // Set up the button to add a new book
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            // Open AddBookActivity when add button is clicked
            val intent = Intent(this@MainActivity, AddBookActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh the book lists
        currentBooksAdapter.books = getBooksFromStorage() // Implement this method to retrieve books
        futureBooksAdapter.books = getBooksFromStorage() // Same here, but filter for future books
    }

    private fun getBooksFromStorage(): List<Book> {
        // This method should return the books from storage, e.g. SharedPreferences, Database, etc.
        return listOf() // Placeholder return
    }
}

class BooksAdapter(private val onItemClick: ((Book) -> Unit)? = null) : RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    var books: List<Book> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount(): Int = books.size

    class BookViewHolder(itemView: View, private val onItemClick: ((Book) -> Unit)?) : RecyclerView.ViewHolder(itemView) {
        fun bind(book: Book) {
            itemView.findViewById<TextView>(R.id.titleTextView).text = book.title
            // Set other views like author and page number

            itemView.setOnClickListener {
                onItemClick?.invoke(book)
            }
        }
    }

    // Inside MainActivity class
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ... other setup code ...

        // Get a reference to the future books RecyclerView
        val futureBooksRecyclerView = findViewById<RecyclerView>(R.id.futureBooksRecyclerView)
        // Assume futureBooks is a MutableList<Book> that you fetch from storage
        val futureBooks = getFutureBooksFromStorage()

        // Use the extension function to set up the RecyclerView
        futureBooksRecyclerView.setupFutureBooks(futureBooks, this) { book ->
            // Handle future book item click
            // For example, open an EditBookActivity with the clicked book details
            val intent = Intent(this, EditBookActivity::class.java)
            intent.putExtra(EditBookActivity.EXTRA_BOOK, book)
            startActivity(intent)
        }
    }

    private fun getFutureBooksFromStorage(): MutableList<Book> {
        // This method should return the future books from storage, e.g. SharedPreferences, Database, etc.
        return mutableListOf() // Placeholder return
    }

}

data class Book(
    var title: String,
    var author: String,
    var page: Int,
    var notes: String
)

