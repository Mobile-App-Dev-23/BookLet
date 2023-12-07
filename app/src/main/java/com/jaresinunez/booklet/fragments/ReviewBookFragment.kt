package com.jaresinunez.booklet.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.jaresinunez.booklet.R
class ReviewBookFragment : Fragment() {
    private lateinit var bookCoverImageView: ImageView
    private  lateinit var byteArray: ByteArray
    private  lateinit var bookReviewTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_review_book, container, false)



        return view
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // 'context' is now available
    }
    companion object {
        @JvmStatic
        fun newInstance(): ReviewBookFragment {
            return ReviewBookFragment()
        }

        const val REQUEST_CODE_FRAGMENT_C = "REVIEw_UPDATE"
    }
}