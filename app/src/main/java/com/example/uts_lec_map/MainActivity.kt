package com.example.uts_lec_map

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BannerPagerAdapter
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.ActivityMainBinding
import com.example.uts_lec_map.models.Book

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set banner ViewPager dengan drawable placeholder
        val bannerImages = listOf(android.R.drawable.ic_menu_gallery, android.R.drawable.ic_menu_camera, android.R.drawable.ic_menu_send)
        val bannerPagerAdapter = BannerPagerAdapter(this, bannerImages)
        binding.viewPager.adapter = bannerPagerAdapter

        // Set RecyclerView untuk trending books
        val trendingBooks = getTrendingBooks()
        binding.trendingRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.trendingRecyclerView.adapter = BookAdapter(this, trendingBooks)

        // Set RecyclerView untuk books berdasarkan preferensi
        val preferenceBooks = getPreferenceBooks()
        binding.preferencesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.preferencesRecyclerView.adapter = BookAdapter(this, preferenceBooks)

        // Bottom navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Navigate to Home (MainActivity)
                    true
                }
                R.id.search -> {
                    // Navigate to SearchPage
                    val intent = Intent(this, SearchPage::class.java)
                    startActivity(intent)
                    finish() // Optional: finish MainActivity if you want to prevent going back to it
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
    }

    private fun getTrendingBooks(): List<Book> {
        return listOf(
            Book("Solo Leveling", android.R.drawable.ic_menu_gallery),
            Book("Harry Potter", android.R.drawable.ic_menu_camera)
        )
    }

    private fun getPreferenceBooks(): List<Book> {
        return listOf(
            Book("Bukan Aku yang Dia Inginkan", android.R.drawable.ic_menu_gallery),
            Book("Harry Potter", android.R.drawable.ic_menu_camera)
        )
    }
}