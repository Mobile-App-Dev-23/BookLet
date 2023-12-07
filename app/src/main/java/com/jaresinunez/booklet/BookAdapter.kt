package com.jaresinunez.booklet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jaresinunez.booklet.databasestuff.ByteArrayHandling.getByteArrayFromResource

const val ITEM_EXTRA = "ITEM_EXTRA"
private const val TAG = "ItemAdapter"

class BookAdapter (private val context: Context, private val items: List<DisplayItem>) :
    RecyclerView.Adapter<BookAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_view, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = items[position]
        holder.bind(book)
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        private val bookTitleTextView = itemView.findViewById<TextView>(R.id.book_title)
        private val bookAuthorTextView = itemView.findViewById<TextView>(R.id.book_author)
        private val bookDescriptionTextView = itemView.findViewById<TextView>(R.id.book_description)
        private val bookRatingTextView = itemView.findViewById<TextView>(R.id.book_rating)
        private val bookCoverImageView = itemView.findViewById<ImageView>(R.id.book_cover)

        init {
            itemView.setOnClickListener(this)
        }

        // TODO: Write a helper method to help set up the onBindViewHolder method
        fun bind(book: DisplayItem) {
            bookTitleTextView.text = book.bookTitle
            bookAuthorTextView.text = book.bookAuthor
            bookDescriptionTextView.text = book.bookDescription
            bookRatingTextView.text = book.bookRating.toString()

            if (book.bookCoverImage != null){
                Glide.with(context)
                    .load(book.bookCoverImage)
                    .into(bookCoverImageView)
            }
            else {
                val byteArray = getByteArrayFromResource(context.resources, R.drawable.book_cover_placeholder)
                Glide.with(context)
                    .load(byteArray)
                    .into(bookCoverImageView)
            }
        }


        override fun onClick(v: View?) {
            /*
            // TODO: Get selected article
            val foodItem = items[absoluteAdapterPosition]

            // TODO: Navigate to Details screen and pass selected article
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(ARTICLE_EXTRA, article)
            context.startActivity(intent)
            */
        }
    }
}