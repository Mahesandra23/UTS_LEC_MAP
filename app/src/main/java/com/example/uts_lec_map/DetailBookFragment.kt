package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.*

class DetailBookFragment : Fragment() {

    // Variabel untuk referensi ke database Firebase
    private lateinit var bookDatabase: DatabaseReference

    // Variabel untuk menyimpan judul buku yang diterima dari Bundle
    private lateinit var bookTitle: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menginflate layout fragment_detail_book
        val view = inflater.inflate(R.layout.fragment_detail_book, container, false)

        // Mengambil judul buku yang dikirim melalui Bundle
        bookTitle = arguments?.getString("bookTitle") ?: ""

        // Bind views dengan komponen UI yang ada pada layout
        val bookTitleTextView = view.findViewById<TextView>(R.id.tv_book_title)  // TextView untuk menampilkan judul buku
        val writerTextView = view.findViewById<TextView>(R.id.tv_writter)  // TextView untuk menampilkan penulis buku
        val synopsisTextView = view.findViewById<TextView>(R.id.tv_book_synopsis)  // TextView untuk menampilkan sinopsis buku
        val bookCoverImageView = view.findViewById<ImageView>(R.id.iv_book_cover)  // ImageView untuk menampilkan gambar sampul buku
        val backButton = view.findViewById<ImageView>(R.id.iv_back_button)  // ImageView sebagai tombol kembali
        val buyButton = view.findViewById<Button>(R.id.btn_buy)  // Button untuk membeli buku

        // Inisialisasi referensi ke Firebase Database, khususnya ke node "buku"
        bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Memeriksa apakah judul buku yang diterima tidak kosong
        if (bookTitle.isNotEmpty()) {
            // Mencari buku di Firebase berdasarkan judul yang diterima
            bookDatabase.orderByChild("judul").equalTo(bookTitle)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    // Ketika data buku berhasil diambil
                    override fun onDataChange(snapshot: DataSnapshot) {
                        // Mengecek apakah ada buku yang ditemukan
                        if (snapshot.exists()) {
                            // Iterasi untuk setiap data buku yang ditemukan
                            for (bookSnapshot in snapshot.children) {
                                // Mengambil data buku dan mengonversinya menjadi objek Book
                                val book = bookSnapshot.getValue(Book::class.java)
                                // Jika data buku tidak null, tampilkan data buku
                                if (book != null) {
                                    // Menampilkan data buku pada komponen UI
                                    bookTitleTextView.text = book.judul
                                    writerTextView.text = book.penulis
                                    synopsisTextView.text = book.sinopsis
                                    buyButton.text = "Buy Rp. ${book.harga}"  // Menampilkan harga pada tombol beli

                                    // Menggunakan Glide untuk memuat gambar sampul buku dari URL
                                    Glide.with(requireContext())
                                        .load(book.cover)
                                        .into(bookCoverImageView)

                                    // Menambahkan aksi pada tombol beli untuk mengarahkan ke PaymentFragment
                                    buyButton.setOnClickListener {
                                        // Membuat Bundle untuk mengirimkan data buku ke PaymentFragment
                                        val bundle = Bundle().apply {
                                            putString("bookTitle", book.judul)
                                            putInt("bookPrice", book.harga)
                                            putString("bookCover", book.cover)
                                        }
                                        // Menavigasi ke PaymentFragment dengan membawa data buku
                                        findNavController().navigate(
                                            R.id.action_detailBookFragment_to_paymentFragment,
                                            bundle
                                        )
                                    }
                                }
                            }
                        } else {
                            // Menampilkan Toast jika buku tidak ditemukan
                            Toast.makeText(
                                requireContext(),
                                "Buku tidak ditemukan",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    // Menangani error jika pengambilan data gagal
                    override fun onCancelled(error: DatabaseError) {
                        // Menampilkan pesan error jika gagal mengambil data buku
                        Toast.makeText(
                            requireContext(),
                            "Gagal mengambil data buku",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } else {
            // Menampilkan pesan jika judul buku tidak ditemukan
            Toast.makeText(requireContext(), "Judul buku tidak ditemukan", Toast.LENGTH_SHORT).show()
        }

        // Menambahkan aksi pada tombol kembali untuk kembali ke HomeFragment
        backButton.setOnClickListener {
            // Menavigasi kembali ke HomeFragment
            findNavController().navigate(R.id.action_detailBookFragment_to_homeFragment)
        }

        return view
    }
}
