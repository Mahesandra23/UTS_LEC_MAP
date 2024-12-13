package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.uts_lec_map.adapters.ListBookAdapter
import com.example.uts_lec_map.databinding.FragmentListBookBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class ListBookFragment : Fragment() {

    // Binding untuk mengakses elemen-elemen UI pada layout fragment_list_book
    private var _binding: FragmentListBookBinding? = null
    private val binding get() = _binding!!

    // Adapter untuk RecyclerView yang menampilkan daftar buku
    private lateinit var bookAdapter: ListBookAdapter

    // List yang menyimpan data buku
    private var bookList: MutableList<Book> = mutableListOf()

    // Referensi ke database Firebase untuk mengambil data buku
    private lateinit var bookDatabase: DatabaseReference

    // Fungsi ini dipanggil untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment_list_book dan mengembalikan root view
        _binding = FragmentListBookBinding.inflate(inflater, container, false)

        // Inisialisasi referensi ke Firebase Database
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Setup RecyclerView untuk menampilkan daftar buku
        setupRecyclerView()

        // Setup Bottom Navigation untuk navigasi antar halaman
        setupBottomNavigation()

        // Setup fungsionalitas pencarian buku berdasarkan judul
        setupSearchFunctionality()

        // Mengambil data buku dari Firebase
        getBooksFromFirebase()

        // Mengembalikan root view dari fragment
        return binding.root
    }

    // Fungsi untuk menyiapkan RecyclerView dengan GridLayoutManager
    private fun setupRecyclerView() {
        // Menggunakan GridLayoutManager untuk menampilkan buku dalam format grid (3 kolom)
        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.layoutManager = gridLayoutManager

        // Menyiapkan adapter untuk RecyclerView
        bookAdapter = ListBookAdapter(requireContext(), bookList)
        binding.recyclerView.adapter = bookAdapter
    }

    // Fungsi untuk menyiapkan Bottom Navigation
    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        // Menandai item bottom navigation yang sedang dipilih (book_list)
        bottomNavigationView.selectedItemId = R.id.book_list
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_book -> {
                    // Navigasi ke AdminFragment jika menu add_book dipilih
                    findNavController().navigate(R.id.action_listBookFragment_to_adminFragment)
                    true
                }
                R.id.book_list -> true // Tidak ada aksi saat book_list dipilih
                else -> false
            }
        }
    }

    // Fungsi untuk menyiapkan fungsionalitas pencarian buku berdasarkan judul
    private fun setupSearchFunctionality() {
        // Mengatur listener pada search bar untuk menangani perubahan teks pencarian
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Menjalankan pencarian saat teks sudah disubmit
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Menjalankan pencarian saat teks pencarian berubah
                performSearch(newText)
                return true
            }
        })
    }

    // Fungsi untuk melakukan pencarian buku berdasarkan query
    private fun performSearch(query: String?) {
        // Menyaring buku berdasarkan judul yang cocok dengan query
        val filteredBooks = if (query.isNullOrEmpty()) {
            bookList // Menampilkan semua buku jika query kosong
        } else {
            // Menyaring buku yang judulnya mengandung query (case-insensitive)
            bookList.filter { it.judul.contains(query, ignoreCase = true) }
        }

        // Memperbarui adapter dengan daftar buku yang sudah difilter
        bookAdapter = ListBookAdapter(requireContext(), filteredBooks)
        binding.recyclerView.adapter = bookAdapter
    }

    // Fungsi untuk mengambil data buku dari Firebase
    private fun getBooksFromFirebase() {
        // Menambahkan listener untuk mengambil data dari Firebase Database
        bookDatabase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Mengosongkan daftar buku sebelum menambahkan data baru
                bookList.clear()

                // Iterasi untuk setiap data buku yang ada di snapshot Firebase
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(Book::class.java)
                    if (book != null) {
                        // Menambahkan buku yang valid ke dalam daftar buku
                        bookList.add(book)
                    }
                }
                // Memberikan tahu adapter bahwa data telah diperbarui
                bookAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Menangani error jika pengambilan data dari Firebase gagal
            }
        })
    }

    // Fungsi ini dipanggil saat fragment dihancurkan
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Membebaskan referensi binding untuk mencegah memory leak
    }
}
