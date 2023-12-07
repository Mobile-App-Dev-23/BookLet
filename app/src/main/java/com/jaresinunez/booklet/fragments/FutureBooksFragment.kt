package com.jaresinunez.booklet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.jaresinunez.booklet.R

class FutureBooksFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_future_books, container, false)

        val addBookButton = view.findViewById<Button>(R.id.add_book_button_future)
        addBookButton.setOnClickListener {
            val addBookFragment = AddBookFragment()
            replaceFragment(addBookFragment)
        }

        return view
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.books_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onResume() {
        super.onResume()
        val fragmentManager = requireActivity().supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.books_frame_layout, FutureBooksFragment())
        transaction.addToBackStack(null)
        transaction.commit()
    }
    companion object {
        @JvmStatic
        fun newInstance(): FutureBooksFragment {
            return FutureBooksFragment()
        }
    }
}