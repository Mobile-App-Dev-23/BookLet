package com.jaresinunez.booklet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaresinunez.booklet.databinding.ActivityMainBinding
import com.jaresinunez.booklet.fragments.CompletedBooksFragment
import com.jaresinunez.booklet.fragments.CurrentBooksFragment
import com.jaresinunez.booklet.fragments.FutureBooksFragment
import java.io.InputStream

private const val TAG = "MainActivity/"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val currentBooksFragment: Fragment = CurrentBooksFragment()
        val completedBooksFragment: Fragment = CompletedBooksFragment()
        val futureBooksFragment: Fragment = FutureBooksFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener {item ->
            lateinit var fragment: Fragment
            when (item.itemId){
                R.id.nav_current -> fragment = currentBooksFragment
                R.id.nav_completed -> fragment = completedBooksFragment
                R.id.nav_future -> fragment = futureBooksFragment
            }
            replaceFragment(fragment)
            true
        }
        bottomNavigationView.selectedItemId = R.id.nav_current

        /*
        val fragmentManager = supportFragmentManager



        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            lateinit var fragment: Fragment
            when (item.itemId) {
                R.id.nav_current -> fragment = currentBooksFragment
                R.id.nav_completed -> fragment = completedBooksFragment
                R.id.nav_future -> fragment = futureBooksFragment
                //else -> fragment = currentBooksFragment
            }
            //fragmentManager.beginTransaction().replace(R.id.books_frame_layout, fragment).commit()
            replaceFragment(fragment)
            true
        }

        // Set default selection
        bottomNavigationView.selectedItemId = R.id.nav_current

         */
    }
    private fun replaceFragment(bookFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.books_frame_layout, bookFragment)
        fragmentTransaction.commit()
    }
}