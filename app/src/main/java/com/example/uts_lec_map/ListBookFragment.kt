package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.ListBookAdapter
import com.example.uts_lec_map.databinding.FragmentListBookBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListBookFragment : Fragment() {
    private var _binding: FragmentListBookBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: ListBookAdapter // Declare bookAdapter
    private var bookList: MutableList<Book> = mutableListOf()
    private lateinit var bookDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBookBinding.inflate(inflater, container, false)

        // Initialize Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        setupRecyclerView()    // Setup RecyclerView
        setupBottomNavigation() // Setup Bottom Navigation
        setupSearchFunctionality() // Setup Search Functionality

        // Fetch books from Firebase
        getBooksFromFirebase()

        return binding.root
    }

    private fun setupRecyclerView() {
        bookAdapter = ListBookAdapter(requireContext(), bookList) // Initialize bookAdapter here
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = bookAdapter
    }

    // Setup Bottom Navigation to navigate between fragments
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.book_list // Mark Book List as selected
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_book -> {
                    findNavController().navigate(R.id.action_listBookFragment_to_adminFragment)
                    true
                }
                R.id.book_list -> {
                    // Already in ListBookFragment
                    true
                }
                // Add more cases if there are other menu items
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
                book.judul.contains(query, ignoreCase = true) // Filter based on title
            }
        }
        bookAdapter = ListBookAdapter(requireContext(), filteredBooks) // Create new adapter with filtered list
        binding.recyclerView.adapter = bookAdapter // Set new adapter to RecyclerView
    }

    // Get all books from Firebase
    private fun getBooksFromFirebase() {
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                bookList.clear() // Clear the list before adding new data
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        bookList.add(book) // Add book to the list
                    }
                }
                bookAdapter.notifyDataSetChanged() // Notify adapter that data has changed
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error here if needed
            }
        })
    }

    // Clean up binding when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
