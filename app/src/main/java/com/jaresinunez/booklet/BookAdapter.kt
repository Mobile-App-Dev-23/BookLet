package com.jaresinunez.booklet

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jaresinunez.booklet.databasestuff.ByteArrayHandling.getByteArrayFromResource
import com.jaresinunez.booklet.fragments.EditBookFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const val ITEM_EXTRA = "ITEM_EXTRA"
private const val TAG = "ItemAdapter"

interface OnDatasetChangedListener {
    fun onDatasetChanged()
}

class BookAdapter (private val context: Context,
                   private val items: List<DisplayItem>,
                   private val listener: OnDatasetChangedListener? = null,
                   private val reviewButtonClickCallback: (DisplayItem) -> Unit,) :
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

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val bookTitleTextView = itemView.findViewById<TextView>(R.id.book_title)
        private val bookAuthorTextView = itemView.findViewById<TextView>(R.id.book_author)
        private val bookDescriptionTextView = itemView.findViewById<TextView>(R.id.book_description)
        private val bookRatingTextView = itemView.findViewById<TextView>(R.id.book_rating)
        private val reviewTitleTextView = itemView.findViewById<TextView>(R.id.review_title_tv)
        private val reviewTextView = itemView.findViewById<TextView>(R.id.review_text_tv)
        private val bookCoverImageView = itemView.findViewById<ImageView>(R.id.book_cover)
        private val bookReviewButton = itemView.findViewById<Button>(R.id.review_book_button)
        private val updateBookReviewButton = itemView.findViewById<Button>(R.id.update_review_button)
        private val buttonGroupLayout = itemView.findViewById<LinearLayout>(R.id.button_group)
        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener {
                editConfirmation(context)
                true
            }
        }

        private fun editConfirmation(context: Context) {
            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Edit Book")
            alertDialogBuilder.setMessage("Would you like to edit your book entry?")
            alertDialogBuilder.setPositiveButton("Continue") { dialog: DialogInterface, _: Int ->
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    (context as MainActivity).setBottomNavigationVisibility(false)
                    val book = items[position]
                    val fragmentManager = (context as AppCompatActivity).supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    val editBookFragment = EditBookFragment.newInstance(
                        context.javaClass.name,
                        book)
                    fragmentTransaction.replace(R.id.books_frame_layout, editBookFragment)
                    fragmentTransaction.addToBackStack(null)
                    fragmentTransaction.commit()
                }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

        fun bind(book: DisplayItem) {
            if(book.bookTitle != null)
                bookTitleTextView.text = book.bookTitle
            else
                bookTitleTextView.text = "Title Unavailable"

            if (book.bookAuthor != null)
                bookAuthorTextView.text = "By " + book.bookAuthor
            else
                bookAuthorTextView.text = "Author Unavailable"

            bookDescriptionTextView.text = book.bookDescription

            if (book.bookRating != null) {
                bookRatingTextView.visibility = View.VISIBLE
                bookRatingTextView.text = book.bookRating.toString()
            }

            if (!book.completed) {
                bookReviewButton.visibility = View.GONE
            }
            else {
                buttonGroupLayout.visibility = View.VISIBLE
                if (book.bookReview.isNullOrEmpty()) { // book is completed but does not have review
                    bookReviewButton.setOnClickListener {
                        reviewButtonClickCallback.invoke(book)
                    }
                }
                else { // book is completed and has review
                    reviewTitleTextView.visibility = View.VISIBLE
                    reviewTextView.text = book.bookReview
                    reviewTextView.visibility = View.VISIBLE
                    updateBookReviewButton.visibility = View.VISIBLE
                    bookReviewButton.visibility = View.GONE
                    updateBookReviewButton.setOnClickListener{
                        reviewButtonClickCallback.invoke(book)
                    }
                }
            }

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

        fun notifyDatasetChanged() {
            notifyDataSetChanged()
            listener?.onDatasetChanged()
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

    private fun deleteBookFromDatabase(book: DisplayItem) {
        val customScope = CoroutineScope(Job() + Dispatchers.Main)
        // Perform the necessary database operation to delete the book
        // Update your UI or trigger any callbacks as needed
        try {
            customScope.launch {
                withContext(Dispatchers.IO) {
                    val bookDao = (context?.applicationContext as ItemApplication).db.bookDaos()
                    val size = bookDao.getBookTableSize().collect()
                    bookDao?.let {
                        book?.let {
                            bookDao.deleteBook(it.toBookEntity().id)
                        }
                    }
                }
                notifyDataSetChanged()
                listener?.onDatasetChanged()
                Log.d("DB-DELETION", book.toString())
            }
        }
        catch (e: Exception) {
            Log.e("CoroutineError", "Coroutine cancelled unexpectedly", e)
        } finally {
            customScope.cancel()
            }
        }
}