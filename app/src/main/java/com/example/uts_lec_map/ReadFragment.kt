package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uts_lec_map.models.Book

class ReadFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_read, container, false)

        // Ambil objek Book dari Bundle
        val book = arguments?.getParcelable<Book>("bookDetails")

        // Bind views
        val bookTitleTextView = view.findViewById<TextView>(R.id.book_title)
        val bookAuthorTextView = view.findViewById<TextView>(R.id.book_author)
        val storyTextView = view.findViewById<TextView>(R.id.bookCerita)
        val backButton = view.findViewById<ImageButton>(R.id.btnBack)

        // Cek jika book ada
        if (book != null) {
            bookTitleTextView.text = book.judul
            bookAuthorTextView.text = book.penulis
            storyTextView.text = book.isi_cerita // Sesuaikan dengan field cerita pada objek Book

            // Set up back button
            backButton.setOnClickListener {
                // Kembali ke halaman sebelumnya
                activity?.onBackPressed()
            }
        } else {
            // Handle jika buku tidak ditemukan
            storyTextView.text = "Buku tidak ditemukan"
        }

        return view
    }
}
