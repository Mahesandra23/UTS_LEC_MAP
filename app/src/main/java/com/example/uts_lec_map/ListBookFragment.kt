package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.ListBookAdapter
import com.example.uts_lec_map.databinding.FragmentListBookBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListBookFragment : Fragment() {
    private var _binding: FragmentListBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: ListBookAdapter
    private var bookList: MutableList<Book> = mutableListOf()
    private lateinit var bookDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBookBinding.inflate(inflater, container, false)

        // Inisialisasi Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        setupRecyclerView()    // Setup RecyclerView
        setupBottomNavigation() // Setup Bottom Navigation
        setupSearchFunctionality() // Setup Search Functionality

        // Ambil data dari Firebase
        getBooksFromFirebase()

        return binding.root
    }

    private fun setupRecyclerView() {
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = gridLayoutManager

        bookAdapter = ListBookAdapter(requireContext(), bookList)
        binding.recyclerView.adapter = bookAdapter
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.book_list
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_book -> {
                    findNavController().navigate(R.id.action_listBookFragment_to_adminFragment)
                    true
                }
                R.id.book_list -> true
                else -> false
            }
        }
    }

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

    private fun performSearch(query: String?) {
        val filteredBooks = if (query.isNullOrEmpty()) {
            bookList
        } else {
            bookList.filter { it.judul.contains(query, ignoreCase = true) }
        }
        bookAdapter = ListBookAdapter(requireContext(), filteredBooks)
        binding.recyclerView.adapter = bookAdapter
    }

    private fun getBooksFromFirebase() {
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book)
                    }
                }
                bookAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
