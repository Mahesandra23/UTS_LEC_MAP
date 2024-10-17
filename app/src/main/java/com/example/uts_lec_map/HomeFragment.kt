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
        bannerDatabase = FirebaseDatabase.getInstance().getReference("banner") // Referensi banner di Firebase

        // Inisialisasi list buku
        trendingBooks = mutableListOf()
        preferenceBooks = mutableListOf()

        // Setup RecyclerViews dan Bottom Navigation
        setupRecyclerViews()  // Pastikan adapter diinisialisasi
        setupBottomNavigation() // Menyiapkan BottomNavigationView

        // Ambil data dari Firebase
        getBooksFromFirebase()
        getBannerFromFirebase() // Ambil data banner dari Firebase

        return binding.root
    }

    private fun setupViewPager(bannerImages: List<String>) {
        if (bannerImages.isNotEmpty()) {
            val bannerPagerAdapter = BannerPagerAdapter(requireContext(), bannerImages)
            binding.viewPager.adapter = bannerPagerAdapter
        }
    }

    private fun getBannerFromFirebase() {
        bannerDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val bannerImages = mutableListOf<String>()
                for (bannerSnapshot in snapshot.children) {
                    // Ambil URL banner dari key-value di dalam snapshot
                    val bannerUrl = bannerSnapshot.getValue(String::class.java)
                    if (bannerUrl != null) {
                        bannerImages.add(bannerUrl) // Tambahkan URL banner ke list
                    }
                }
                // Update ViewPager dengan gambar yang diambil dari Firebase
                setupViewPager(bannerImages)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal mengambil data banner.", Toast.LENGTH_SHORT).show()
            }
        })
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
        bottomNavigationView.selectedItemId = R.id.home // Mark Home as selected
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
                        if (book.harga > 50000) { // Misalnya buku dengan harga lebih dari 50.000
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
