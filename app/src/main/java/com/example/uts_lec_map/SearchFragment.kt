package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentSearchBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter
    private var bookList: MutableList<Book> = mutableListOf()
    private lateinit var bookDatabase: DatabaseReference
    private lateinit var purchaseDatabase: DatabaseReference
    private lateinit var currentUserId: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        // Initialize Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        setupRecyclerView() // Setup RecyclerView
        setupBottomNavigation() // Setup Bottom Navigation
        setupSearchFunctionality() // Setup Search Functionality

        // Fetch books from Firebase
        getBooksFromFirebase()
        purchaseDatabase = FirebaseDatabase.getInstance().getReference("purchases")
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


        return binding.root
    }

    // Setup RecyclerView with BookAdapter
    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(
            requireContext(),
            bookList
        ) { bookTitle, isPurchased -> // Updated callback to match signature
            // Navigate to Book Details or perform any action
            val action = if (isPurchased) {
                SearchFragmentDirections.actionSearchFragmentToDetailBookFragment(bookTitle)
            } else {
                SearchFragmentDirections.actionSearchFragmentToDetailBookBayarFragment(bookTitle)
            }
            findNavController().navigate(action)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
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
        binding.searchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
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
                book.judul.contains(query, ignoreCase = true) // Filter by title
            }
        }

        bookAdapter = BookAdapter(
            requireContext(),
            filteredBooks
        ) { bookTitle, isPurchased -> // Updated callback
            purchaseDatabase.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val purchasedBooks = snapshot.children.map { it.child("bookTitle").value.toString() }
                    val action = if (purchasedBooks.contains(bookTitle)) {
                        SearchFragmentDirections.actionSearchFragmentToDetailBookFragment(bookTitle)
                    } else {
                        SearchFragmentDirections.actionSearchFragmentToDetailBookBayarFragment(bookTitle)
                    }
                    findNavController().navigate(action)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Gagal memeriksa pembelian.", Toast.LENGTH_SHORT).show()
                }
            })
        }

        binding.recyclerView.adapter = bookAdapter
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
