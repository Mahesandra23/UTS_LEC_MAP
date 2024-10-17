package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentSearchBinding
import com.example.uts_lec_map.models.Book
import com.google.android.material.bottomnavigation.BottomNavigationView

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookAdapter: BookAdapter
    private var bookList: List<Book> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        setupRecyclerView()    // Setup RecyclerView
        setupBottomNavigation() // Setup Bottom Navigation
        setupSearchFunctionality() // Setup Search Functionality

        return binding.root
    }

    // Setup RecyclerView with BookAdapter
    private fun setupRecyclerView() {
        bookList = getAllBooks() // Initialize book list
        bookAdapter = BookAdapter(requireContext(), bookList)
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
                book.title.contains(query, ignoreCase = true)
            }
        }
        bookAdapter.updateBooks(filteredBooks) // Update adapter with filtered list
    }

    // Get all books (you can replace this with actual data source)
    private fun getAllBooks(): List<Book> {
        return listOf(
            Book("Solo Leveling", android.R.drawable.ic_menu_gallery),
            Book("Harry Potter", android.R.drawable.ic_menu_camera),
            Book("Bukan Aku yang Dia Inginkan", android.R.drawable.ic_menu_gallery)
        )
    }

    // Clean up binding when view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}