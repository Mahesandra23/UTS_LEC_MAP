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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var trendingBooks: MutableList<Book>
    private lateinit var preferenceBooks: MutableList<Book>
    private lateinit var bookDatabase: DatabaseReference
    private lateinit var purchaseDatabase: DatabaseReference
    private lateinit var bannerDatabase: DatabaseReference

    // Adapter untuk RecyclerView
    private lateinit var trendingAdapter: BookAdapter
    private lateinit var preferenceAdapter: BookAdapter

    private lateinit var currentUserId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inisialisasi Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")
        bannerDatabase = FirebaseDatabase.getInstance().getReference("banner")
        purchaseDatabase = FirebaseDatabase.getInstance().getReference("purchases")
        currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

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
        val bannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)
        val bannerPagerAdapter = BannerPagerAdapter(requireContext(), bannerImages)
        binding.viewPager.adapter = bannerPagerAdapter
    }

    private fun setupRecyclerViews() {
        binding.trendingRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = BookAdapter(requireContext(), trendingBooks) { bookTitle ->
            navigateToDetail(bookTitle)
        }
        binding.trendingRecyclerView.adapter = trendingAdapter

        binding.preferencesRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        preferenceAdapter = BookAdapter(requireContext(), preferenceBooks) { bookTitle ->
            navigateToDetail(bookTitle)
        }
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

    private fun getBooksFromFirebase() {
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trendingBooks.clear()
                preferenceBooks.clear()

                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        trendingBooks.add(book)
                        if (book.harga > 50000) {
                            preferenceBooks.add(book)
                        }
                    }
                }

                trendingAdapter.notifyDataSetChanged()
                preferenceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal mengambil data dari Firebase.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToDetail(bookTitle: String) {
        purchaseDatabase.child(currentUserId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val purchasedBooks = snapshot.children.map { it.child("bookTitle").value.toString() }
                val action = if (purchasedBooks.contains(bookTitle)) {
                    R.id.action_homeFragment_to_detailBookFragment
                } else {
                    R.id.action_homeFragment_to_detailBookBayarFragment
                }
                val bundle = Bundle().apply { putString("bookTitle", bookTitle) }
                findNavController().navigate(action, bundle)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal memeriksa pembelian.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
