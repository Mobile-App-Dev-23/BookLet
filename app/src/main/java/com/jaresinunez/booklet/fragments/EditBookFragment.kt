package com.jaresinunez.booklet.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.jaresinunez.booklet.Constants
import com.jaresinunez.booklet.Constants.REQUEST_KEY
import com.jaresinunez.booklet.Constants.RESULT_KEY
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.MainActivity
import com.jaresinunez.booklet.R
import com.jaresinunez.booklet.databasestuff.AppDatabase
import com.jaresinunez.booklet.databasestuff.ByteArrayHandling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditBookFragment : Fragment() {
    private val customScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var view: View
    private lateinit var submitButton: Button
    private lateinit var bookCoverImageView: ImageView
    private lateinit var editImageButton: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var bookTitleET: EditText
    private lateinit var bookAuthorET: EditText
    private lateinit var bookDescriptionET: EditText
    private lateinit var reviewET: EditText
    private lateinit var ratingGroupLinearLayout: LinearLayout
    private lateinit var reviewGroupLinearLayout: LinearLayout
    private lateinit var ratingET: EditText
    private val PICK_IMAGE_REQUEST = 1
    lateinit var byteArray: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_edit_book, container, false)
        submitButton = view.findViewById(R.id.update_book_button)
        bookCoverImageView = view.findViewById(R.id.edit_book_cover_image)
        editImageButton = view.findViewById(R.id.edit_image_button)
        radioGroup = view.findViewById(R.id.radio_group_edit)
        bookTitleET = view.findViewById(R.id.edit_book_title_edittext)
        bookAuthorET = view.findViewById(R.id.edit_book_author_edittext)
        bookDescriptionET = view.findViewById(R.id.edit_book_description_edittext)
        reviewET = view.findViewById(R.id.edit_book_review_edittext)
        ratingGroupLinearLayout = view.findViewById(R.id.rating_group_layout)
        reviewGroupLinearLayout = view.findViewById(R.id.review_group)
        ratingET = view.findViewById(R.id.edit_rating_ET)

        val arg = arguments?.getSerializable(BOOK_ARG) as? DisplayItem
        if (arg != null) { initialize(view, arg) }

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.current_edit -> {
                    reviewGroupLinearLayout.visibility = View.GONE
                    ratingGroupLinearLayout.visibility = View.GONE
                }
                R.id.completed_edit -> {
                    reviewGroupLinearLayout.visibility = View.VISIBLE
                    ratingGroupLinearLayout.visibility = View.VISIBLE
                }
                R.id.future_edit -> {
                    reviewGroupLinearLayout.visibility = View.GONE
                    ratingGroupLinearLayout.visibility = View.GONE
                }
            }
        }

        editImageButton.setOnClickListener {
            openGallery()
        }

        submitButton.setOnClickListener {
            if (bookTitleET.text.isNullOrEmpty() || bookAuthorET.text.isNullOrEmpty()) {
                showRequiredSectionAlert()
            } else {
                val currentRB: RadioButton = view.findViewById(R.id.current_edit)
                val completedRB: RadioButton = view.findViewById(R.id.completed_edit)
                val futureRB: RadioButton = view.findViewById(R.id.future_edit)

                val review: String?
                val rating: Double?
                if (completedRB.isChecked) {
                    review = reviewET.text.toString()
                    rating = ratingET.text.toString().toDouble()
                } else {
                    review = null
                    rating = null
                }

                if (byteArray.isEmpty()){
                    byteArray = ByteArrayHandling.getByteArrayFromResource(
                        requireContext().resources,
                        R.drawable.book_cover_placeholder
                    )
                }

                val updatedBook = arg!!.copy(
                    bookTitle = bookTitleET.text.toString(),
                    bookAuthor = bookAuthorET.text.toString(),
                    bookDescription = bookDescriptionET.text.toString(),
                    bookReview = review,
                    bookRating = rating,
                    bookPageCount = arg.bookPageCount,
                    bookCoverImage = byteArray,
                    bookPurchaseURL = arg.bookPurchaseURL,
                    current = currentRB.isChecked,
                    completed = completedRB.isChecked,
                    future = futureRB.isChecked
                ).toBookEntity()

                customScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            val bookDao =
                                AppDatabase.getInstance(requireContext().applicationContext as ItemApplication)
                                    .bookDaos()

                            updatedBook?.let {
                                bookDao.updateBook(it)
                                Log.d("DB UPDATE", it.toString())
                                setFragmentResult(
                                    REQUEST_KEY,
                                    bundleOf(RESULT_KEY to Constants.REQUEST_CODE_UPDATE_BOOK_FRAGMENT)
                                )
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("CoroutineError", "Coroutine cancelled unexpectedly", e)

                    } finally {
                        customScope.cancel()
                    }
                }
                (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()            }
        }

        return view
    }

    private fun initialize(view: View, arg: DisplayItem){
        var book = arg?.let {
            DisplayItem(
                it.id,
                it.bookTitle,
                it.bookAuthor,
                it.bookDescription,
                it.bookReview,
                it.bookRating,
                it.bookPageCount,
                it.bookCoverImage,
                it.bookPurchaseURL,
                it.current,
                it.completed,
                it.future)
        }
        if (book != null){
            bookTitleET.setText(book.bookTitle)
            bookAuthorET.setText(book.bookAuthor)
            bookDescriptionET.setText(book.bookDescription)

            if (book.bookCoverImage != null) {
                byteArray = book.bookCoverImage!!
                Glide.with(requireContext())
                    .load(book.bookCoverImage)
                    .into(bookCoverImageView)
            }
            else{
                byteArray = ByteArrayHandling.getByteArrayFromResource(
                    requireContext().resources,
                    R.drawable.book_cover_placeholder
                )
            }

            val currentRB: RadioButton = view.findViewById(R.id.current_edit)
            val completedRB: RadioButton = view.findViewById(R.id.completed_edit)
            val futureRB: RadioButton = view.findViewById(R.id.future_edit)

            if (book.completed){
                ratingGroupLinearLayout.visibility = View.VISIBLE
                reviewGroupLinearLayout.visibility = View.VISIBLE
                completedRB.isChecked = true
                if (book.bookRating != null){
                    ratingET.setText(book.bookRating.toString())
                    reviewET.setText(book.bookReview.toString())
                } else {
                    ratingET.setText(null)
                    reviewET.setText(null)
                }
            } else if (book.current){
                currentRB.isChecked = true
                ratingGroupLinearLayout.visibility = View.GONE
                reviewGroupLinearLayout.visibility = View.GONE
            } else{
                futureRB.isChecked = true
                ratingGroupLinearLayout.visibility = View.GONE
                reviewGroupLinearLayout.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            if (selectedImageUri != null) {
                byteArray = ByteArrayHandling.getByteArrayFromImageUri(requireContext(), selectedImageUri!!)
            }
            bookCoverImageView.setImageURI(selectedImageUri)
        }
    }

    private fun showRequiredSectionAlert() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
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
        private const val BOOK_ARG = "book_arg"
        private const val ACTIVITY_CLASS_NAME = "activity_class_name"

        fun newInstance(className: String, book: DisplayItem): EditBookFragment {
            val fragment = EditBookFragment()
            val args = Bundle()
            args.putSerializable(BOOK_ARG, book)
            args.putString(ACTIVITY_CLASS_NAME, className)
            fragment.arguments = args
            return fragment
        }
    }
}