package com.jaresinunez.booklet.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaresinunez.booklet.Constants
import com.jaresinunez.booklet.Constants.REQUEST_CODE_REVIEW_FRAGMENT
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ITEM_EXTRA
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.MainActivity
import com.jaresinunez.booklet.R
import com.jaresinunez.booklet.databasestuff.AppDatabase
import com.jaresinunez.booklet.databasestuff.entities.BookEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReviewBookFragment : Fragment() {
    private val customScope = CoroutineScope(Job() + Dispatchers.Main)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_review_book, container, false)
        val submitButton: Button = view.findViewById(R.id.submit)
        val bookRatingEditText: EditText = view.findViewById(R.id.rating)
        val bookReviewEditText: EditText = view.findViewById(R.id.review)
        val titleTexView: TextView = view.findViewById(R.id.book_title_tv)
        val authorTextView: TextView = view.findViewById(R.id.author)
        val descriptionTextView: TextView = view.findViewById(R.id.description)
        val bookCoverImageView: ImageView = view.findViewById(R.id.book_cover_image_review)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Book Review"

        val book = arguments?.getSerializable(ITEM_EXTRA) as? DisplayItem
        if (book != null) {
            if (book.bookTitle != null)
                titleTexView.text = book.bookTitle
            else
                titleTexView.text = "Title Unavailable"

            if (book.bookAuthor != null)
                authorTextView.text = book.bookAuthor
            else
                authorTextView.text = "Author Unavailable"

            if (book.bookDescription != null)
                descriptionTextView.text = book.bookDescription
            else
                descriptionTextView.text = "Description Unavailable"

            if (book.bookCoverImage != null)
                Glide.with(requireContext())
                    .load(book.bookCoverImage)
                    .into(bookCoverImageView)
        }

        submitButton.setOnClickListener {
            if (bookRatingEditText.text.isNotEmpty() && bookReviewEditText.text.isNotEmpty()) {
                if (book != null){

                    val updatedBook = book.copy(
                        bookReview = bookReviewEditText.text.toString(),
                        bookRating = bookRatingEditText.text.toString().toDouble()
                    ).toBookEntity()

                    customScope.launch {
                        try {
                            withContext(Dispatchers.IO) {
                                val bookDao = AppDatabase.getInstance(requireContext().applicationContext as ItemApplication)
                                    .bookDaos()

                                updatedBook?.let {
                                    bookDao.updateBook(it)
                                    Log.d("DB UPDATE", it.toString())
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("CoroutineError", "Coroutine cancelled unexpectedly", e)
                        } finally {
                            customScope.cancel()
                            setFragmentResult(
                                Constants.REQUEST_KEY,
                                bundleOf(Constants.RESULT_KEY to REQUEST_CODE_REVIEW_FRAGMENT)
                            )
                        }
                    }
                    (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                } else {
                    Log.e("VALUE_ERROR", "Book is null")
                    (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.popBackStack()
                }
            } else {
                showRequiredSectionAlert()
            }
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }
    private fun showRequiredSectionAlert() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Required Section")
        alertDialogBuilder.setMessage("Make sure all fields are filled.")
        alertDialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
    companion object {
        fun newInstance(): ReviewBookFragment {
            return ReviewBookFragment()
        }
    }
}