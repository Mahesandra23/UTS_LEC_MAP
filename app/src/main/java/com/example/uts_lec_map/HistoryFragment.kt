package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.adapters.BookForReadAdapter
import com.example.uts_lec_map.databinding.FragmentHistoryBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookForReadAdapter: BookForReadAdapter // Ganti nama variabel
    private var bookList: MutableList<Book> = mutableListOf()
    private lateinit var paymentDatabase: DatabaseReference
    private lateinit var bookDatabase: DatabaseReference
    private val userId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Inisialisasi Firebase Database
        paymentDatabase = FirebaseDatabase.getInstance().getReference("payments")
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        setupRecyclerView()
        fetchPurchasedBooks()

        return binding.root
    }

    private fun setupRecyclerView() {
        // Menggunakan requireContext() untuk mendapatkan Context yang tidak nullable
        bookForReadAdapter = BookForReadAdapter(requireContext(), bookList)

        // Set LayoutManager dan Adapter untuk RecyclerView
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = bookForReadAdapter // Gunakan bookForReadAdapter
    }

    private fun fetchPurchasedBooks() {
        // Ambil daftar transaksi untuk user berdasarkan userId
        paymentDatabase.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val purchasedBooks = snapshot.children.mapNotNull { it.child("judul").value as? String }
                        fetchBooksByTitles(purchasedBooks)  // Mengambil buku berdasarkan judul
                    } else {
                        showNoBooksMessage()  // Menampilkan pesan jika tidak ada buku yang dibeli
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    private fun fetchBooksByTitles(bookTitles: List<String>) {
        bookList.clear()
        for (bookTitle in bookTitles) {
            bookDatabase.orderByChild("judul").equalTo(bookTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (bookSnapshot in snapshot.children) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                bookList.add(book)
                            }
                        }
                        bookForReadAdapter.notifyDataSetChanged()  // Notifikasi adapter jika data berubah
                        toggleNoBooksMessage()  // Toggle visibility message
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle error
                    }
                })
        }
    }

    private fun showNoBooksMessage() {
        binding.tvNoBooks.visibility = View.VISIBLE
        binding.rvHistory.visibility = View.GONE
    }

    private fun toggleNoBooksMessage() {
        if (bookList.isEmpty()) {
            showNoBooksMessage()
        } else {
            binding.tvNoBooks.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
