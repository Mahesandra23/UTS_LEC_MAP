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
import com.example.uts_lec_map.databinding.FragmentReadBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReadFragment : Fragment() {

    private lateinit var bookDatabase: DatabaseReference
    private lateinit var bookTitle: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_read, container, false)

        // Ambil judul buku dari Bundle
        bookTitle = arguments?.getString("bookTitle") ?: ""

        // Bind views
        val storyTextView = view.findViewById<TextView>(R.id.bookCerita)
        val backButton = view.findViewById<ImageView>(R.id.btnBack)
        val bookTitleTextView = view.findViewById<TextView>(R.id.book_title)

        // Inisialisasi Firebase
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Ambil data isi cerita dari Firebase
        if (bookTitle.isNotEmpty()) {
            bookDatabase.orderByChild("judul").equalTo(bookTitle).addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (bookSnapshot in snapshot.children) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                // Set isi cerita ke TextView
                                storyTextView.text = book.isi_cerita
                                bookTitleTextView.text = book.judul
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Isi cerita tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Gagal mengambil isi cerita", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "Judul buku tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        // Handle back button
        backButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        return view
    }
}
