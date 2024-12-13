package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uts_lec_map.models.Book

class ReadFragment : Fragment() {

    // Fungsi untuk menampilkan tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Mengambil layout fragment_read.xml dan menghubungkannya dengan fragment ini
        val view = inflater.inflate(R.layout.fragment_read, container, false)

        // Mengambil objek Book dari Bundle yang dikirimkan saat fragment ini dipanggil
        val book = arguments?.getParcelable<Book>("bookDetails")

        // Menghubungkan elemen-elemen UI dengan variabel-variabel di dalam kode
        val bookTitleTextView = view.findViewById<TextView>(R.id.book_title)
        val bookAuthorTextView = view.findViewById<TextView>(R.id.book_author)
        val storyTextView = view.findViewById<TextView>(R.id.bookCerita)
        val backButton = view.findViewById<ImageButton>(R.id.btnBack)
        val scrollView = view.findViewById<ScrollView>(R.id.scrollView)
        val btnBackToTop = view.findViewById<Button>(R.id.btnBackToTop)

        // Memeriksa apakah objek 'book' ada (tidak null)
        if (book != null) {
            // Menampilkan informasi dari objek 'book' ke tampilan UI
            bookTitleTextView.text = book.judul // Menampilkan judul buku
            bookAuthorTextView.text = book.penulis // Menampilkan penulis buku

            // Memformat isi cerita agar lebih rapi
            val paragraphs = book.isi_cerita.split(".") // Memisahkan isi cerita berdasarkan tanda titik (.)
            val formattedText = paragraphs.joinToString("\n\n") { it.trim() } // Menambahkan spasi antar paragraf
            storyTextView.text = formattedText // Menampilkan teks yang telah diformat

            // Menangani klik pada tombol 'back' untuk kembali ke halaman sebelumnya
            backButton.setOnClickListener {
                // Fungsi untuk kembali ke fragment atau aktivitas sebelumnya
                activity?.onBackPressed()
            }
        } else {
            // Jika objek book tidak ditemukan, menampilkan pesan error
            storyTextView.text = "Buku tidak ditemukan"
        }

        // Logika untuk tombol 'Back to Top'
        btnBackToTop.visibility = View.GONE // Tombol ini disembunyikan pada awalnya

        // Mengatur listener untuk mendeteksi pergerakan scroll pada ScrollView
        scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            // Menampilkan tombol 'Back to Top' saat scroll lebih dari 200px
            if (scrollY > 200) { // Threshold scroll
                btnBackToTop.visibility = View.VISIBLE
            } else {
                btnBackToTop.visibility = View.GONE
            }
        }

        // Menangani klik pada tombol 'Back to Top' untuk menggulir halaman ke atas
        btnBackToTop.setOnClickListener {
            // Menggerakkan scroll ke posisi paling atas
            scrollView.smoothScrollTo(0, 0)
        }

        // Mengembalikan tampilan fragment ini
        return view
    }
}
