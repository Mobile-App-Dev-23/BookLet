package com.jaresinunez.booklet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.jaresinunez.booklet.R

class CompletedBooksFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_completed_books, container, false)

        val addBookButton = view.findViewById<Button>(R.id.add_book_button)
        addBookButton.setOnClickListener {
            val addBookFragment = AddBookFragment()
            replaceFragment(addBookFragment)
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = activity?.supportFragmentManager
        fragmentManager?.beginTransaction()
            ?.replace(R.id.books_frame_layout, fragment)
            ?.addToBackStack(null)
            ?.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(): CompletedBooksFragment {
            return CompletedBooksFragment()
        }
    }
}