package com.example.uts_lec_map

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.ActivitySearchPageBinding
import com.example.uts_lec_map.models.Book

class SearchPage : AppCompatActivity() {

    private lateinit var binding: ActivitySearchPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding
        binding = ActivitySearchPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val bookList = getBookList() // Ambil daftar buku
        binding.recyclerView.adapter = BookAdapter(this, bookList)

        // Bottom navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Navigate to Home
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.search -> {
                    // Stay on Search
                    true
                }
                R.id.history -> {
                    // Navigate to History
                    true
                }
                R.id.profile -> {
                    // Navigate to Profile
                    true
                }
                else -> false
            }
        }

// Set the selected item to search
        binding.bottomNavigation.selectedItemId = R.id.search

    }

    private fun getBookList(): List<Book> {
        return listOf(
            Book("Book 1", android.R.drawable.ic_menu_gallery),
            Book("Book 2", android.R.drawable.ic_menu_camera),
            Book("Book 3", android.R.drawable.ic_menu_send)
        )
    }
}