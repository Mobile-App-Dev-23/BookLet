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
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.jaresinunez.booklet.Constants.bookAdapter
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.R
import com.jaresinunez.booklet.databasestuff.AppDatabase
import com.jaresinunez.booklet.databasestuff.ByteArrayHandling
import com.jaresinunez.booklet.databasestuff.ByteArrayHandling.getByteArrayFromImageUri
import com.jaresinunez.booklet.databasestuff.entities.BookEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBookFragment : Fragment() {
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var bookCoverImageView: ImageView
    private lateinit var uploadImageButton: Button
    private  lateinit var byteArray: ByteArray
    private  var imageURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)
        val addBookButton: Button = view.findViewById(R.id.add_book_button)
        val radioGroup: RadioGroup = view.findViewById(R.id.radio_group)
        val bookTitleET: EditText = view.findViewById(R.id.book_title_edittext)
        val bookAuthorET: EditText = view.findViewById(R.id.book_author_edittext)
        val bookDescriptionET: EditText = view.findViewById(R.id.book_description_edittext)
        uploadImageButton = view.findViewById(R.id.upload_image_button)
        bookCoverImageView = view.findViewById(R.id.book_cover_image)

        if (isAdded){
            byteArray = ByteArrayHandling.getByteArrayFromResource(requireContext().resources, R.drawable.book_cover_placeholder)
        }

        uploadImageButton.setOnClickListener {
            openGallery()
        }

        addBookButton.setOnClickListener {
            if (radioGroup.checkedRadioButtonId != -1 &&
                (bookTitleET.text.isNotEmpty() || bookAuthorET.text.isNotEmpty())){
                val currentRB: RadioButton = view.findViewById(R.id.current)
                val completedRB: RadioButton = view.findViewById(R.id.completed)
                val futureRB: RadioButton = view.findViewById(R.id.future)

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val bookDao = AppDatabase.getInstance(context?.applicationContext as ItemApplication).bookDaos()
                        if (byteArray.isEmpty()) {
                            byteArray = ByteArrayHandling.getByteArrayFromResource(requireContext().resources, R.drawable.book_cover_placeholder)
                        }
                        val newBook = BookEntity(
                            id = 0,
                            title = bookTitleET.text.toString(),
                            author = bookAuthorET.text.toString(),
                            description = bookDescriptionET.text.toString(),
                            review = null,
                            rating = null,
                            pageCount = null,
                            coverImage = byteArray,
                            purchaseUrl = null,
                            current = currentRB.isChecked,
                            completed = completedRB.isChecked,
                            future = futureRB.isChecked
                        )
                        bookDao.insertBook(newBook)
                        Log.d("DB ADDITION", newBook.toString())
                    }
                    withContext(Dispatchers.Main) {
                        bookAdapter.notifyDataSetChanged()
                    }
                }

                setFragmentResult("requestKey", bundleOf("resultKey" to REQUEST_CODE_FRAGMENT_B))

                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.popBackStack()
            } else {
                showRequiredSectionAlert()
            }
        }

        return view
    }
    private fun showRequiredSectionAlert() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Required Section")
        alertDialogBuilder.setMessage("Please select an option.")
        alertDialogBuilder.setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
                imageURI = selectedImageUri
                if (isAdded)
                    byteArray = getByteArrayFromImageUri(context, imageURI!!)
            }
            bookCoverImageView.setImageURI(selectedImageUri)
            bookCoverImageView.visibility = View.VISIBLE
            uploadImageButton.visibility = View.GONE
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 'context' is now available
    }
    private fun refreshFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.books_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    companion object {
        @JvmStatic
        fun newInstance(): AddBookFragment {
            return AddBookFragment()
        }

        const val REQUEST_CODE_FRAGMENT_B = "NEW_BOOK"
    }
}