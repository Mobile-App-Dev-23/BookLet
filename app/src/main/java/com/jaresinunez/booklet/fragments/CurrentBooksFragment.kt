package com.jaresinunez.booklet.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaresinunez.booklet.BookAdapter
import com.jaresinunez.booklet.Constants.bookAdapter
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CurrentBooksFragment : Fragment() {
    private lateinit var bookViewsRecyclerView: RecyclerView
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
        val addBookButton = view.findViewById<Button>(R.id.add_book_button_current)

        // Add these configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        bookViewsRecyclerView = view.findViewById(R.id.book_recycler_view_current)
        bookViewsRecyclerView.layoutManager = layoutManager
        bookViewsRecyclerView.setHasFixedSize(true)
        bookAdapter = BookAdapter(
            view.context,
            books,
        ) {}
        bookViewsRecyclerView.adapter = bookAdapter

        bookViewsRecyclerView.layoutManager = LinearLayoutManager(activity).also {
            val dividerItemDecoration = DividerItemDecoration(activity, it.orientation)
            bookViewsRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        updateAdapterWithDB(view)


        addBookButton.setOnClickListener {
            val addBookFragment = AddBookFragment()
            replaceFragment(addBookFragment)
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener("requestKey") { _, result ->
            // Handle the result received from Fragment B
            val result = result.getString("resultKey")

            // Do something with the result
            Log.d("CurrentBooksFragment", "Received result: $result")
            updateAdapterWithDB(view)
        }
    }
    private fun updateAdapterWithDB(view: View) {
        // Perform database operations based on the result from FragmentB
        // Update your UI or trigger additional actions as needed
        lifecycleScope.launch {
            try {
            (context?.applicationContext as ItemApplication).db.bookDaos().getAllCurrents()
                .collect { databaseList ->
                    val mappedList = databaseList.map { entity ->
                        DisplayItem(
                            entity.id,
                            entity.title,
                            entity.author,
                            entity.description,
                            entity.review,
                            entity.rating,
                            entity.pageCount,
                            entity.coverImage,
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
            } catch (e: Exception) {
                // Handle the exception (e.g., log, show an error message)
                Log.e("DatabaseError", "Error loading books from database", e)
            }
        }
        if(books.isNotEmpty())
            view.setBackgroundColor(Color.WHITE)
        else
            view.setBackgroundColor(Color.TRANSPARENT)
    }
    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.books_frame_layout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 'context' is now available
    }
    companion object {
        fun newInstance(): CurrentBooksFragment{
            return CurrentBooksFragment()
        }
    }
}