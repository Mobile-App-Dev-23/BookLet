package com.jaresinunez.booklet.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
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
import android.widget.RatingBar
import androidx.activity.addCallback
import com.bumptech.glide.Glide
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ITEM_EXTRA
import com.jaresinunez.booklet.MainActivity
import com.jaresinunez.booklet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

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
    private lateinit var ratingGroupLinearLayout: LinearLayout
    private lateinit var ratingBar: RatingBar

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
        ratingGroupLinearLayout = view.findViewById(R.id.rating_group_layout)
        ratingBar = view.findViewById(R.id.edit_rating)

        val arg = arguments?.getSerializable(ITEM_EXTRA) as? DisplayItem
        if (arg != null) { initialize(view, arg) }



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

            if (book.bookCoverImage != null)
                Glide.with(requireContext())
                    .load(book.bookCoverImage)
                    .into(bookCoverImageView)

            val currentRB: RadioButton = view.findViewById(R.id.current_edit)
            val completedRB: RadioButton = view.findViewById(R.id.completed_edit)
            val futureRB: RadioButton = view.findViewById(R.id.future_edit)

            if (book.completed){
                ratingGroupLinearLayout.visibility = View.VISIBLE
                completedRB.isChecked = true
                if (book.bookRating != null){
                    ratingBar.rating = book.bookRating!!.toFloat()
                } else
                    ratingBar.rating = 0.0F
            } else if (book.current){
                currentRB.isChecked = true
            } else{
                futureRB.isChecked = true
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
        fun newInstance(book: DisplayItem): EditBookFragment{
            return EditBookFragment()
        }
    }
}