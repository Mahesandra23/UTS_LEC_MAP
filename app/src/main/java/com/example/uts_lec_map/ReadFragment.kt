package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
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
        val scrollView = view.findViewById<ScrollView>(R.id.scrollView)
        val btnBackToTop = view.findViewById<Button>(R.id.btnBackToTop)

        // Cek jika book ada
        if (book != null) {
            bookTitleTextView.text = book.judul
            bookAuthorTextView.text = book.penulis

            // Format isi cerita agar lebih rapi
            val paragraphs = book.isi_cerita.split(".") // Pisahkan berdasarkan tanda titik
            val formattedText = paragraphs.joinToString("\n\n") { it.trim() } // Tambahkan spasi antar paragraf
            storyTextView.text = formattedText // Set teks yang telah diformat

            // Set up back button
            backButton.setOnClickListener {
                // Kembali ke halaman sebelumnya
                activity?.onBackPressed()
            }
        } else {
            // Handle jika buku tidak ditemukan
            storyTextView.text = "Buku tidak ditemukan"
        }

        // Logika untuk tombol Back to Top
        btnBackToTop.visibility = View.GONE // Awalnya disembunyikan
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // Tampilkan tombol jika pengguna scroll ke bawah
            if (scrollY > 200) { // Adjust threshold sesuai kebutuhan
                btnBackToTop.visibility = View.VISIBLE
            } else {
                btnBackToTop.visibility = View.GONE
            }
        }

        btnBackToTop.setOnClickListener {
            // Scroll ke atas saat tombol diklik
            scrollView.smoothScrollTo(0, 0)
        }

        return view
    }
}
