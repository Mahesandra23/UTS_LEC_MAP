package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uts_lec_map.adapters.BookAdapter
import com.example.uts_lec_map.adapters.BookForReadAdapter
import com.example.uts_lec_map.databinding.FragmentHistoryBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    // Binding untuk mengakses elemen-elemen UI pada layout fragment_history
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    // Inisialisasi adapter untuk RecyclerView yang menampilkan daftar buku yang telah dibeli
    private lateinit var bookForReadAdapter: BookForReadAdapter
    // List untuk menyimpan data buku yang telah dibeli
    private var bookList: MutableList<Book> = mutableListOf()

    // Referensi ke database Firebase untuk pembayaran dan buku
    private lateinit var paymentDatabase: DatabaseReference
    private lateinit var bookDatabase: DatabaseReference

    // Mendapatkan ID pengguna yang sedang login menggunakan FirebaseAuth
    private val userId: String by lazy { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }

    // Fungsi ini dipanggil untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment_history dan mengembalikan root view
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Inisialisasi referensi database Firebase
        paymentDatabase = FirebaseDatabase.getInstance().getReference("payments")
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Menyiapkan RecyclerView untuk menampilkan daftar buku yang telah dibeli
        setupRecyclerView()

        // Mengambil daftar buku yang dibeli oleh pengguna
        fetchPurchasedBooks()

        // Mengatur bottom navigation
        setupBottomNavigation()

        return binding.root
    }

    // Fungsi untuk menyiapkan RecyclerView dengan adapter dan layout manager
    private fun setupRecyclerView() {
        // Menggunakan requireContext() untuk mendapatkan context yang tidak nullable
        bookForReadAdapter = BookForReadAdapter(requireContext(), bookList)

        // Mengatur layout manager dan adapter pada RecyclerView
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = bookForReadAdapter
    }

    // Fungsi untuk mengambil daftar buku yang telah dibeli oleh pengguna
    private fun fetchPurchasedBooks() {
        // Mengambil daftar transaksi dari Firebase berdasarkan userId
        paymentDatabase.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Jika ada data transaksi yang ditemukan
                    if (snapshot.exists()) {
                        // Mengambil judul buku dari transaksi yang ditemukan
                        val purchasedBooks = snapshot.children.mapNotNull { it.child("judul").value as? String }
                        // Mengambil buku berdasarkan judul yang dibeli
                        fetchBooksByTitles(purchasedBooks)
                    } else {
                        // Menampilkan pesan jika tidak ada buku yang dibeli
                        showNoBooksMessage()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Menangani error jika pembacaan data gagal
                }
            })
    }

    // Fungsi untuk mengambil data buku berdasarkan judul yang dibeli
    private fun fetchBooksByTitles(bookTitles: List<String>) {
        // Mengosongkan daftar buku sebelum menambahkan buku yang baru
        bookList.clear()

        // Loop untuk mengambil setiap buku berdasarkan judul
        for (bookTitle in bookTitles) {
            // Mengambil data buku dari Firebase berdasarkan judul
            bookDatabase.orderByChild("judul").equalTo(bookTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Menambahkan buku yang ditemukan ke dalam daftar buku
                        for (bookSnapshot in snapshot.children) {
                            val book = bookSnapshot.getValue(Book::class.java)
                            if (book != null) {
                                bookList.add(book)
                            }
                        }
                        // Memberi tahu adapter bahwa data telah berubah dan RecyclerView perlu di-refresh
                        bookForReadAdapter.notifyDataSetChanged()
                        // Mengatur visibilitas pesan jika tidak ada buku yang ditemukan
                        toggleNoBooksMessage()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Menangani error jika pembacaan data gagal
                    }
                })
        }
    }

    // Fungsi untuk mengatur navigasi bottom navigation
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        // Set item yang sedang dipilih adalah history
        bottomNavigationView.selectedItemId = R.id.history
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Menavigasi ke HomeFragment
                    findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    // Menavigasi ke SearchFragment
                    findNavController().navigate(R.id.action_historyFragment_to_searchFragment)
                    true
                }
                R.id.history -> true // Tombol History tidak mengubah apapun
                R.id.profile -> {
                    // Menavigasi ke ProfileFragment
                    findNavController().navigate(R.id.action_historyFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    // Fungsi untuk menampilkan pesan jika tidak ada buku yang ditemukan
    private fun showNoBooksMessage() {
        binding.tvNoBooks.visibility = View.VISIBLE
        binding.rvHistory.visibility = View.GONE
    }

    // Fungsi untuk mengatur visibilitas pesan jika daftar buku kosong atau ada buku
    private fun toggleNoBooksMessage() {
        if (bookList.isEmpty()) {
            showNoBooksMessage()  // Menampilkan pesan jika tidak ada buku
        } else {
            binding.tvNoBooks.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
        }
    }

    // Fungsi ini dipanggil saat fragment dihancurkan, digunakan untuk membersihkan binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Membebaskan referensi binding untuk mencegah memory leak
    }
}
