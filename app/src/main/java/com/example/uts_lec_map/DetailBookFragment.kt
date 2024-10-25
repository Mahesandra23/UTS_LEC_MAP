package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*

// DetailBookFragment.kt
// DetailBookFragment.kt
class DetailBookFragment : Fragment() {

    private lateinit var bookDatabase: DatabaseReference
    private lateinit var bookTitle: String  // Judul buku yang akan ditampilkan

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_book, container, false)

        // Ambil judul buku dari Bundle
        bookTitle = arguments?.getString("bookTitle") ?: ""

        // Bind views
        val bookTitleTextView = view.findViewById<TextView>(R.id.tv_book_title)
        val writerTextView = view.findViewById<TextView>(R.id.tv_writter)
        val synopsisTextView = view.findViewById<TextView>(R.id.tv_book_synopsis)
        val bookCoverImageView = view.findViewById<ImageView>(R.id.iv_book_cover)
        val backButton = view.findViewById<ImageView>(R.id.iv_back_button)

        // Inisialisasi Firebase
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Ambil data buku dari Firebase berdasarkan judul
        if (bookTitle.isNotEmpty()) {
            bookDatabase.orderByChild("judul").equalTo(bookTitle).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                // Set data buku ke views
                                bookTitleTextView.text = book.judul
                                writerTextView.text = book.penulis
                                synopsisTextView.text = book.sinopsis
                                // Load cover image menggunakan Glide
                                Glide.with(requireContext())
                                    .load(book.cover)
                                    .into(bookCoverImageView)
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Buku tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Gagal mengambil data buku", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Judul buku tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        // Handle back button
        backButton.setOnClickListener {
            // Kembali ke halaman sebelumnya
            requireActivity().onBackPressed()
        }

        return view
    }
}

