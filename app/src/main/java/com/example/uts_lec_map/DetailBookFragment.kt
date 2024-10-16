package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class DetailBookFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_book, container, false)

        // Bind views
        val backButton = view.findViewById<ImageView>(R.id.iv_back_button)
        val bookTitleTextView = view.findViewById<TextView>(R.id.tv_book_title)
        val synopsisTextView = view.findViewById<TextView>(R.id.tv_book_synopsis)
        val buyButton = view.findViewById<Button>(R.id.btn_buy)
        val writerTextView = view.findViewById<TextView>(R.id.tv_writter)

        // Set data to views (static or dynamic based on your implementation)
        bookTitleTextView.text = "HARRY POTTER: CURSED CHILD"
        writerTextView.text = "By: J.K. Rowling"
        synopsisTextView.text = "It was always difficult being Harry Potter and it isn’t much easier now that he is an overworked employee of the Ministry of Magic, a husband and father of three school-age children. While Harry grapples with a past that refuses to stay where it belongs, his youngest son Albus must struggle with the weight of a family legacy he never wanted. As past and present fuse ominously, both father and son learn the uncomfortable truth: sometimes, darkness comes from unexpected places."

        // Handle back button click
        backButton.setOnClickListener {
            findNavController().popBackStack()  // Navigate back
        }

        // Handle buy button click
        buyButton.setOnClickListener {
            Toast.makeText(activity, "Proceed to buy the book", Toast.LENGTH_SHORT).show()
            // Implement logic to initiate purchase
        }

        return view
    }
}
