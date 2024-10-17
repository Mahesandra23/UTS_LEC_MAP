package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentHistoryBinding
import com.example.uts_lec_map.models.Book
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var booksAdapter: BookAdapter
    private var booksList: MutableList<Book> = mutableListOf() // To hold the purchased books

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Set up Bottom Navigation
        setupBottomNavigation()

        // Set up RecyclerView
        setupRecyclerView()

        // Load books from Firebase
        loadPurchasedBooks()

        return binding.root
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.history // Mark History as selected

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    findNavController().navigate(R.id.action_historyFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    // Already in HistoryFragment
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.action_historyFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(context)
        booksAdapter = BookAdapter(requireContext(), booksList) { book ->
            // Handle book click if needed
        }
        binding.rvHistory.adapter = booksAdapter
    }

    private fun loadPurchasedBooks() {
        // Assuming you have a Firestore collection named "purchasedBooks"
        val db = FirebaseFirestore.getInstance()
        db.collection("purchasedBooks").get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val title = document.getString("title") ?: ""
                    val coverImage = R.drawable.harry_potter
                    // Use a default cover image or modify to get from Firestore
                    booksList.add(Book(title, coverImage))
                }
                updateUI()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    private fun updateUI() {
        if (booksList.isEmpty()) {
            binding.tvNoBooks.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.tvNoBooks.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            booksAdapter.notifyDataSetChanged() // Notify the adapter of data changes
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
