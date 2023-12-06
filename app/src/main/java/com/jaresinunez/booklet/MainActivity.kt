package com.jaresinunez.booklet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaresinunez.booklet.databinding.ActivityMainBinding
import com.jaresinunez.booklet.fragments.CompletedBooksFragment
import com.jaresinunez.booklet.fragments.CurrentBooksFragment
import com.jaresinunez.booklet.fragments.FutureBooksFragment

private const val TAG = "MainActivity/"

class MainActivity : AppCompatActivity() {
    private lateinit var booksRecyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    private val books = mutableListOf<DisplayItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val completedBooksFragment: Fragment = CompletedBooksFragment()
        val currentBooksFragment: Fragment = CurrentBooksFragment()
        val futureBooksFragment: Fragment = FutureBooksFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.nav_current -> fragment = currentBooksFragment
                R.id.nav_completed -> fragment = completedBooksFragment
                R.id.nav_future -> fragment = futureBooksFragment
            }
            replaceFragment(fragment)
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_current

    }
    private fun replaceFragment(bookFragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.books_frame_layout, bookFragment)
            .commit()
    }
}