package com.jaresinunez.booklet.fragments

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaresinunez.booklet.BookAdapter
import com.jaresinunez.booklet.Constants.REQUEST_KEY
import com.jaresinunez.booklet.Constants.RESULT_KEY
import com.jaresinunez.booklet.DisplayItem
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

class FutureBooksFragment : Fragment(), OnDatasetChangedListener {
    private val customScope = CoroutineScope(Job() + Dispatchers.Main)
    private lateinit var view: View
    private lateinit var bookAdapter: BookAdapter
    private lateinit var bookViewsRecyclerView: RecyclerView
    private val books = mutableListOf<DisplayItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_future_books, container, false)
        val addBookButton = view.findViewById<Button>(R.id.add_book_button_future)

        val layoutManager = LinearLayoutManager(context)
        bookViewsRecyclerView = view.findViewById(R.id.book_recycler_view_future)
        bookViewsRecyclerView.layoutManager = layoutManager
        bookViewsRecyclerView.setHasFixedSize(true)
        bookAdapter = BookAdapter(
            view.context,
            books,
        ){}
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
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFragmentResultListener(REQUEST_KEY) { _, result ->
            val result = result.getString(RESULT_KEY)

            (requireActivity() as MainActivity).setBottomNavigationVisibility(true)
            Log.d("FutureBooksFragment", "Received result: $result")
            bookAdapter.notifyDataSetChanged()
        }
    }
    private fun updateAdapterWithDB(view: View) {
        customScope.launch {
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
                            bookAdapter.notifyDataSetChanged()
                        }
                    }
            } catch (e: Exception) {
                Log.e("DatabaseError|FUTURE", "Error loading books from database", e)
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
        fun newInstance(): FutureBooksFragment {
            return FutureBooksFragment()
        }
    }
    override fun onDatasetChanged() {
        updateAdapterWithDB(view)
    }
}