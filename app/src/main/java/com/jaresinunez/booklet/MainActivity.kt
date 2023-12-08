package com.jaresinunez.booklet

import android.annotation.SuppressLint
import android.content.ClipData.Item
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaresinunez.booklet.databasestuff.entities.BookEntity
import com.jaresinunez.booklet.databinding.ActivityMainBinding
import com.jaresinunez.booklet.fragments.CompletedBooksFragment
import com.jaresinunez.booklet.fragments.CurrentBooksFragment
import com.jaresinunez.booklet.fragments.FutureBooksFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.InputStream

private const val TAG = "MainActivity/"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val currentBooksFragment: Fragment = CurrentBooksFragment()
        val completedBooksFragment: Fragment = CompletedBooksFragment()
        val futureBooksFragment: Fragment = FutureBooksFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation_main)

        supportActionBar?.setDefaultDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My Bookshelf"

        bottomNavigationView.setOnItemSelectedListener {item ->
            lateinit var fragment: Fragment
            when (item.itemId){
                R.id.nav_current -> {
                    setBottomNavigationVisibility(true)
                    fragment = currentBooksFragment
                }
                R.id.nav_completed -> {
                    setBottomNavigationVisibility(true)
                    fragment = completedBooksFragment
                }
                R.id.nav_future -> {
                    setBottomNavigationVisibility(true)
                    fragment = futureBooksFragment
                }
            }
            replaceFragment(fragment)
            true
        }
        bottomNavigationView.selectedItemId = R.id.nav_current
    }
    private fun replaceFragment(bookFragment: Fragment) {
        if (bookFragment is CurrentBooksFragment)
            hideEmptyShelf((applicationContext as ItemApplication).db.bookDaos().getAllCurrents())
        else if (bookFragment is CompletedBooksFragment)
            hideEmptyShelf((applicationContext as ItemApplication).db.bookDaos().getAllCompleted())
        else if (bookFragment is FutureBooksFragment)
            hideEmptyShelf((applicationContext as ItemApplication).db.bookDaos().getAllFuture())

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.books_frame_layout, bookFragment)
        fragmentTransaction.commit()
    }

    fun setBottomNavigationVisibility(isVisible: Boolean) {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_main)
        bottomNavigationView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun hideEmptyShelf(flowList: Flow<List<BookEntity>>){
        lifecycleScope.launch {
            launch {
                flowList
                    .collect { list ->
                        if (list.size > 0)
                            findViewById<ImageView>(R.id.empty_shelf_img).visibility = View.GONE
                        else
                            findViewById<ImageView>(R.id.empty_shelf_img).visibility = View.VISIBLE
                    }
            }
        }
    }
}