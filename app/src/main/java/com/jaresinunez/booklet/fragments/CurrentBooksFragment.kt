package com.jaresinunez.booklet.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaresinunez.booklet.BookAdapter
import com.jaresinunez.booklet.Constants.bookAdapter
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.R
import com.jaresinunez.booklet.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CurrentBooksFragment : Fragment() {
    private lateinit var bookViewsRecyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private val books = mutableListOf<DisplayItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_current_books, container, false)

        val addBookButton = view.findViewById<Button>(R.id.add_book_button)
        addBookButton.setOnClickListener {
            val addBookFragment = AddBookFragment()
            replaceFragment(addBookFragment)
        }

        // Add these configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        bookViewsRecyclerView = view.findViewById(R.id.book_recycler_view)
        bookViewsRecyclerView.layoutManager = layoutManager
        bookViewsRecyclerView.setHasFixedSize(true)
        bookAdapter = BookAdapter(
            view.context,
            books
        )
        bookViewsRecyclerView.adapter = bookAdapter

        lifecycleScope.launch {
            (context?.applicationContext as ItemApplication).db.bookDaos().getAllCurrents()
                .collect { databaseList ->
                    val mappedList = databaseList.map { entity ->
                        DisplayItem(
                            entity.id,
                            entity.title,
                            entity.author,
                            entity.description,
                            entity.rating,
                            entity.pageCount,
                            entity.coverUrl,
                            entity.purchaseUrl,
                            entity.current,
                            entity.completed,
                            entity.future
                        )
                    }

                    withContext(Dispatchers.Main) {
                        books.clear()
                        books.addAll(mappedList)
                        bookAdapter.notifyDataSetChanged()
                    }
                }
        }

        bookViewsRecyclerView.layoutManager = LinearLayoutManager(activity).also {
            val dividerItemDecoration = DividerItemDecoration(activity, it.orientation)
            bookViewsRecyclerView.addItemDecoration(dividerItemDecoration)
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
        fun newInstance(): CurrentBooksFragment{
            return CurrentBooksFragment()
        }
    }
}