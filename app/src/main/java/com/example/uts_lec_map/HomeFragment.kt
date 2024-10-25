package com.example.uts_lec_map

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BannerPagerAdapter
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentHomeBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var trendingBooks: MutableList<Book>
    private lateinit var preferenceBooks: MutableList<Book>
    private lateinit var bookDatabase: DatabaseReference
    private lateinit var bannerDatabase: DatabaseReference

    // Adapter untuk RecyclerView
    private lateinit var trendingAdapter: BookAdapter
    private lateinit var preferenceAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inisialisasi Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")
        bannerDatabase = FirebaseDatabase.getInstance().getReference("banner")

        // Inisialisasi list buku
        trendingBooks = mutableListOf()
        preferenceBooks = mutableListOf()

        // Setup RecyclerViews dan Bottom Navigation
        setupRecyclerViews()
        setupViewPager()
        setupBottomNavigation()

        // Ambil data dari Firebase
        getBooksFromFirebase()
        return binding.root
    }

    private fun setupViewPager() {
        // Set banner ViewPager dengan drawable placeholder
        val bannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
        val bannerPagerAdapter = BannerPagerAdapter(requireContext(), bannerImages)
        binding.viewPager.adapter = bannerPagerAdapter
    }

    private fun setupRecyclerViews() {
        // Set RecyclerView untuk trending books
        binding.trendingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = BookAdapter(requireContext(), trendingBooks)
        binding.trendingRecyclerView.adapter = trendingAdapter

        // Set RecyclerView untuk books berdasarkan preferensi
        binding.preferencesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        preferenceAdapter = BookAdapter(requireContext(), preferenceBooks)
        binding.preferencesRecyclerView.adapter = preferenceAdapter
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true
                R.id.search -> {
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    // HomeFragment.kt
    private fun getBooksFromFirebase() {
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trendingBooks.clear() // Kosongkan list trending sebelum menambahkan data baru
                preferenceBooks.clear() // Kosongkan list preference juga

                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        trendingBooks.add(book) // Menambahkan semua buku ke trendingBooks

                        // Kriteria untuk preferensi
                        if (book.harga > 50000) {
                            preferenceBooks.add(book)
                        }
                    }
                }
                // Beri tahu adapter bahwa data telah berubah
                trendingAdapter.notifyDataSetChanged()
                preferenceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal mengambil data dari Firebase.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
