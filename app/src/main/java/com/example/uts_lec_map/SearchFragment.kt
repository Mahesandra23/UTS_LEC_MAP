package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentSearchBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchFragment : Fragment() {
    // Variabel untuk menyimpan binding yang digunakan untuk mengakses elemen UI
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // Variabel untuk adapter dan list buku
    private lateinit var bookAdapter: BookAdapter
    private var bookList: MutableList<Book> = mutableListOf()

    // Referensi ke Firebase Database
    private lateinit var bookDatabase: DatabaseReference

    // Fungsi ini dipanggil saat fragment pertama kali dibuat untuk menyiapkan UI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginisialisasi binding untuk mengakses elemen-elemen UI di layout fragment_search.xml
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Inisialisasi referensi Firebase Database di "buku"
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Menyiapkan RecyclerView, Bottom Navigation, dan fungsionalitas pencarian
        setupRecyclerView()    // Setup RecyclerView
        setupBottomNavigation() // Setup Bottom Navigation
        setupSearchFunctionality() // Setup Search Functionality

        // Ambil data buku dari Firebase
        getBooksFromFirebase()

        // Mengembalikan root view yang terikat dengan fragment ini
        return binding.root
    }

    // Fungsi untuk menyiapkan RecyclerView dengan menggunakan BookAdapter
    private fun setupRecyclerView() {
        // Menggunakan GridLayoutManager untuk menampilkan item dalam dua kolom
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = gridLayoutManager

        // Inisialisasi BookAdapter dan menghubungkannya dengan data buku
        bookAdapter = BookAdapter(requireContext(), bookList)
        binding.recyclerView.adapter = bookAdapter
    }

    // Fungsi untuk menyiapkan Bottom Navigation agar pengguna dapat bernavigasi antara fragment
    private fun setupBottomNavigation() {
        // Mengambil referensi ke BottomNavigationView dari layout
        val bottomNavigationView = binding.bottomNavigation
        // Menandai item "search" sebagai item yang dipilih
        bottomNavigationView.selectedItemId = R.id.search

        // Menangani perubahan item pada BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Navigasi ke fragment Home
                    findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    // Sudah berada di SearchFragment, tidak perlu melakukan apa-apa
                    true
                }
                R.id.history -> {
                    // Navigasi ke fragment History
                    findNavController().navigate(R.id.action_searchFragment_to_historyFragment)
                    true
                }
                R.id.profile -> {
                    // Navigasi ke fragment Profile
                    findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi untuk menyiapkan fungsionalitas pencarian menggunakan SearchView
    private fun setupSearchFunctionality() {
        // Menambahkan listener pada SearchView untuk mendeteksi perubahan teks
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            // Ketika pengguna mengirimkan query pencarian
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query) // Melakukan pencarian berdasarkan query
                return true
            }

            // Ketika teks pencarian berubah
            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText) // Melakukan pencarian dengan teks baru
                return true
            }
        })
    }

    // Fungsi untuk melakukan pencarian dan memfilter daftar buku berdasarkan query
    private fun performSearch(query: String?) {
        // Jika query kosong, tampilkan semua buku, jika tidak, lakukan filter berdasarkan judul buku
        val filteredBooks = if (query.isNullOrEmpty()) {
            bookList // Tampilkan semua buku jika query kosong
        } else {
            bookList.filter { book ->
                book.judul.contains(query, ignoreCase = true) // Memfilter buku berdasarkan judul
            }
        }
        // Membuat adapter baru dengan daftar buku yang difilter dan memperbarui RecyclerView
        bookAdapter = BookAdapter(requireContext(), filteredBooks)
        binding.recyclerView.adapter = bookAdapter
    }

    // Fungsi untuk mengambil semua data buku dari Firebase
    private fun getBooksFromFirebase() {
        // Menambahkan listener untuk mendeteksi perubahan data di Firebase
        bookDatabase.addValueEventListener(object : ValueEventListener {
            // Fungsi ini dipanggil ketika data di Firebase berhasil diambil
            override fun onDataChange(snapshot: DataSnapshot) {
                // Kosongkan daftar buku terlebih dahulu
                bookList.clear()
                // Menambahkan setiap buku yang ditemukan di snapshot ke dalam daftar buku
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book) // Menambahkan buku ke dalam list
                    }
                }
                // Memberitahu adapter bahwa data telah diperbarui
                bookAdapter.notifyDataSetChanged()
            }

            // Fungsi ini dipanggil jika terjadi kesalahan saat mengambil data
            override fun onCancelled(error: DatabaseError) {
                // Tampilkan pesan error jika ada masalah saat mengambil data
            }
        })
    }

    // Fungsi untuk membersihkan binding ketika fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Menghapus referensi ke binding untuk menghindari memory leaks
    }
}
