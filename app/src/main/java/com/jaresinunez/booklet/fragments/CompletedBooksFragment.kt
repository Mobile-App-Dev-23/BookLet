package com.jaresinunez.booklet.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jaresinunez.booklet.BookAdapter
import com.jaresinunez.booklet.Constants.REQUEST_KEY
import com.jaresinunez.booklet.Constants.RESULT_KEY
import com.jaresinunez.booklet.DisplayItem
import com.jaresinunez.booklet.ITEM_EXTRA
import com.jaresinunez.booklet.ItemApplication
import com.jaresinunez.booklet.MainActivity
import com.jaresinunez.booklet.OnDatasetChangedListener
import com.jaresinunez.booklet.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CompletedBooksFragment : Fragment(), OnDatasetChangedListener {
    private val customScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var bookAdapter: BookAdapter
    private lateinit var view: View
    private lateinit var bookViewsRecyclerView: RecyclerView
    private val books = mutableListOf<DisplayItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_completed_books, container, false)
        val addBookButton = view.findViewById<Button>(R.id.add_book_button_complete)

        // Add these configurations for the recyclerView and to configure the adapter
        val layoutManager = LinearLayoutManager(context)
        bookViewsRecyclerView = view.findViewById(R.id.book_recycler_view_complete)
        bookViewsRecyclerView.layoutManager = layoutManager
        bookViewsRecyclerView.setHasFixedSize(true)

        bookAdapter = BookAdapter(
            view.context,
            books) { book ->
            openReviewBookFragment(book)
        }

        bookViewsRecyclerView.adapter = bookAdapter

        bookViewsRecyclerView.layoutManager = LinearLayoutManager(activity).also {
            val dividerItemDecoration = DividerItemDecoration(activity, it.orientation)
            bookViewsRecyclerView.addItemDecoration(dividerItemDecoration)
        }

        updateAdapterWithDB(view)

        addBookButton.setOnClickListener {
            (requireActivity() as MainActivity).setBottomNavigationVisibility(false)
            val addBookFragment = AddBookFragment()
            replaceFragment(addBookFragment)
        }

        return view
    }
    private fun openReviewBookFragment(book: DisplayItem) {
        // Code to open ReviewBookFragment with the selected book
        val reviewFragment = ReviewBookFragment()
        val bundle = Bundle()
        bundle.putSerializable(ITEM_EXTRA, book)
        reviewFragment.arguments = bundle

        // Use FragmentTransaction to replace the fragment
        (requireActivity() as MainActivity).setBottomNavigationVisibility(false)
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.books_frame_layout, reviewFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(REQUEST_KEY) { _, result ->
            val result = result.getString(RESULT_KEY)

            //(requireActivity() as MainActivity).setBottomNavigationVisibility(true)
            Log.d("CompletedBooksFragment", "Received result: $result")
            bookAdapter.notifyDataSetChanged()
        }
    }
    private fun updateAdapterWithDB(view: View) {
        customScope.launch {
            try {
                (context?.applicationContext as ItemApplication).db.bookDaos().getAllCompleted()
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
                Log.e("DatabaseError|CURRENT", "Error loading books from database", e)
                e.printStackTrace()
            } finally {
                customScope.cancel()
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
    }
    companion object {
        fun newInstance(): CompletedBooksFragment {
            return CompletedBooksFragment()
        }
    }
    override fun onDatasetChanged() {
        updateAdapterWithDB(view)
    }
}