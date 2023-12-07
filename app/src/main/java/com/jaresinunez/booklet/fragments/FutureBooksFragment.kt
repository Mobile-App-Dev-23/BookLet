package com.jaresinunez.booklet.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaresinunez.booklet.BookAdapter
import com.jaresinunez.booklet.Constants
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FutureBooksFragment : Fragment() {
    private lateinit var bookViewsRecyclerView: RecyclerView
    private val books = mutableListOf<DisplayItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_future_books, container, false)
        val addBookButton = view.findViewById<Button>(R.id.add_book_button_future)

        // Add these configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        bookViewsRecyclerView = view.findViewById(R.id.book_recycler_view_future)
        bookViewsRecyclerView.layoutManager = layoutManager
        bookViewsRecyclerView.setHasFixedSize(true)
        Constants.bookAdapter = BookAdapter(
            view.context,
            books,
        ){}
        bookViewsRecyclerView.adapter = Constants.bookAdapter

        bookViewsRecyclerView.layoutManager = LinearLayoutManager(activity).also {
            val dividerItemDecoration = DividerItemDecoration(activity, it.orientation)
            bookViewsRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        updateAdapterWithDB()

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
            Log.d("FutureBooksFragment", "Received result: $result")
            updateAdapterWithDB()
        }
    }
    private fun updateAdapterWithDB() {
        // Perform database operations based on the result from FragmentB
        // Update your UI or trigger additional actions as needed
        lifecycleScope.launch {
            try {
                (context?.applicationContext as ItemApplication).db.bookDaos().getAllFuture()
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
                            Constants.bookAdapter.notifyDataSetChanged()
                        }
                    }
            } catch (e: Exception) {
                // Handle the exception (e.g., log, show an error message)
                Log.e("DatabaseError", "Error loading books from database", e)
            }
        }
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
        @JvmStatic
        fun newInstance(): FutureBooksFragment {
            return FutureBooksFragment()
        }
    }
}