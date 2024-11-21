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
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter
    private var bookList: MutableList<Book> = mutableListOf()
    private lateinit var bookDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Inisialisasi Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        setupRecyclerView()    // Setup RecyclerView
        setupBottomNavigation() // Setup Bottom Navigation
        setupSearchFunctionality() // Setup Search Functionality

        // Ambil data dari Firebase
        getBooksFromFirebase()

        return binding.root
    }

    // Setup RecyclerView with BookAdapter
    private fun setupRecyclerView() {
        // Menggunakan GridLayoutManager untuk membuat 2 kolom
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = gridLayoutManager

        // Inisialisasi adapter
        bookAdapter = BookAdapter(requireContext(), bookList)
        binding.recyclerView.adapter = bookAdapter
    }


    // Setup Bottom Navigation to navigate between fragments
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.search // Mark Search as selected
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_searchFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    // Already in SearchFragment
                    true
                }
                R.id.history -> {
                    findNavController().navigate(R.id.action_searchFragment_to_historyFragment)
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.action_searchFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    // Setup search functionality with SearchView
    private fun setupSearchFunctionality() {
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText)
                return true
            }
        })
    }

    // Perform search and filter the book list based on the query
    private fun performSearch(query: String?) {
        val filteredBooks = if (query.isNullOrEmpty()) {
            bookList // Show all books if query is empty
        } else {
            bookList.filter { book ->
                book.judul.contains(query, ignoreCase = true) // Memfilter berdasarkan judul
            }
        }
        bookAdapter = BookAdapter(requireContext(), filteredBooks) // Buat adapter baru dengan daftar yang difilter
        binding.recyclerView.adapter = bookAdapter // Set adapter baru ke RecyclerView
    }

    // Get all books from Firebase
    private fun getBooksFromFirebase() {
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear() // Kosongkan list sebelum menambahkan data baru
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book) // Menambahkan buku ke list
                    }
                }
                bookAdapter.notifyDataSetChanged() // Beri tahu adapter bahwa data telah diperbarui
            }

            override fun onCancelled(error: DatabaseError) {
                // Tampilkan pesan error
            }
        })
    }

    // Clean up binding when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
