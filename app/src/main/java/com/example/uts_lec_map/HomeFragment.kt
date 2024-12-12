package com.example.uts_lec_map

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_lec_map.adapters.BannerPagerAdapter
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.databinding.FragmentHomeBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    // Binding untuk mengakses elemen-elemen UI pada layout fragment_home
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi list buku untuk trending dan preferensi
    private lateinit var trendingBooks: MutableList<Book>
    private lateinit var preferenceBooks: MutableList<Book>

    // Referensi ke database Firebase untuk mengambil data buku dan banner
    private lateinit var bookDatabase: DatabaseReference
    private lateinit var bannerDatabase: DatabaseReference

    // Adapter untuk RecyclerView yang menampilkan buku trending dan preferensi
    private lateinit var trendingAdapter: BookAdapter
    private lateinit var preferenceAdapter: BookAdapter

    // Handler dan Runnable untuk mengatur auto-scrolling pada ViewPager banner
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    // Fungsi ini dipanggil untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment_home dan mengembalikan root view
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inisialisasi referensi ke Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")
        bannerDatabase = FirebaseDatabase.getInstance().getReference("banner")

        // Inisialisasi list buku untuk trending dan preferensi
        trendingBooks = mutableListOf()
        preferenceBooks = mutableListOf()

        // Setup RecyclerViews dan Bottom Navigation
        setupRecyclerViews()
        setupViewPager()
        setupBottomNavigation()

        // Ambil data buku dari Firebase
        getBooksFromFirebase()

        // Mengembalikan root view dari fragment
        return binding.root
    }

    // Fungsi untuk menyiapkan ViewPager yang menampilkan banner
    private fun setupViewPager() {
        // Daftar gambar banner yang akan ditampilkan
        val bannerImages = listOf(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3)

        // Menggunakan BannerPagerAdapter untuk menampilkan gambar banner
        val bannerPagerAdapter = BannerPagerAdapter(requireContext(), bannerImages)
        binding.viewPager.adapter = bannerPagerAdapter

        // Handler untuk auto-scroll banner
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            var currentPage = 0
            override fun run() {
                if (bannerImages.isNotEmpty()) {
                    currentPage = (currentPage + 1) % bannerImages.size
                    binding.viewPager.setCurrentItem(currentPage, true) // Ganti halaman banner
                    handler.postDelayed(this, 5000) // Ganti halaman setiap 5 detik
                }
            }
        }
        // Memulai rotasi banner setelah 9 detik
        handler.postDelayed(runnable, 9000)
    }

    // Fungsi untuk menyiapkan RecyclerView untuk menampilkan daftar buku
    private fun setupRecyclerViews() {
        // Menyiapkan RecyclerView untuk trending books (menggunakan layout horizontal)
        binding.trendingRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        trendingAdapter = BookAdapter(requireContext(), trendingBooks)
        binding.trendingRecyclerView.adapter = trendingAdapter

        // Menyiapkan RecyclerView untuk preference books (menggunakan layout horizontal)
        binding.preferencesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        preferenceAdapter = BookAdapter(requireContext(), preferenceBooks)
        binding.preferencesRecyclerView.adapter = preferenceAdapter
    }

    // Fungsi untuk menyiapkan bottom navigation
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        // Menandai item bottom navigation yang sedang dipilih (home)
        bottomNavigationView.selectedItemId = R.id.home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> true // Tidak ada aksi saat home dipilih
                R.id.search -> {
                    // Navigasi ke SearchFragment
                    findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    // Navigasi ke HistoryFragment
                    findNavController().navigate(R.id.action_homeFragment_to_historyFragment)
                    true
                }
                R.id.profile -> {
                    // Navigasi ke ProfileFragment
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi untuk mengambil data buku dari Firebase
    private fun getBooksFromFirebase() {
        // Mengambil data buku dari Firebase
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Mengosongkan list buku sebelum menambahkan data baru
                trendingBooks.clear()
                preferenceBooks.clear()

                // Variabel untuk menghitung jumlah buku yang ditambahkan
                var trendingCount = 0
                var preferenceCount = 0

                // Iterasi untuk setiap data buku yang ada di snapshot Firebase
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        // Menambahkan buku ke daftar trendingBooks, maksimal 10 buku
                        if (trendingCount < 10) {
                            trendingBooks.add(book)
                            trendingCount++
                        }

                        // Kriteria untuk buku preferensi berdasarkan harga dan batas 10 buku
                        if (book.harga in 50000..150000 && preferenceCount < 10) {
                            preferenceBooks.add(book) // Menambahkan buku ke daftar preferensi
                            preferenceCount++
                        }
                    }
                }

                // Memberikan tahu adapter bahwa data telah diperbarui
                trendingAdapter.notifyDataSetChanged()
                preferenceAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Menampilkan Toast jika terjadi error saat mengambil data dari Firebase
                Toast.makeText(requireContext(), "Gagal mengambil data dari Firebase.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Fungsi ini dipanggil saat fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(runnable) // Menghentikan auto-scroll banner saat fragment dihancurkan
        _binding = null  // Membebaskan referensi binding untuk mencegah memory leak
    }
}
