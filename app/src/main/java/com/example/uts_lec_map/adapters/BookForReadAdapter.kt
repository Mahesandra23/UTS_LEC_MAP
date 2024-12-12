package com.example.uts_lec_map.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts_lec_map.R
import com.example.uts_lec_map.models.Book

// Adapter untuk menampilkan daftar buku dalam RecyclerView
// Adapter bertanggung jawab untuk menghubungkan data (dalam hal ini daftar buku) dengan tampilan di UI
class BookForReadAdapter(private val context: Context, private val bookList: List<Book>) :
    RecyclerView.Adapter<BookForReadAdapter.BookViewHolder>() {

    // Fungsi ini digunakan untuk membuat view holder yang berisi tampilan untuk item buku
    // dipanggil setiap kali item baru perlu ditampilkan dalam RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        // Menggunakan LayoutInflater untuk membuat tampilan item_history yang sesuai
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return BookViewHolder(view)  // Mengembalikan BookViewHolder yang berisi tampilan item buku
    }

    // Fungsi ini digunakan untuk mengikat data buku dengan tampilan (view) yang ada
    // dipanggil setiap kali data buku diubah atau ketika tampilan perlu diperbarui
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]  // Mendapatkan buku dari daftar berdasarkan posisi
        holder.bind(book)  // Mengikat data buku ke tampilan
    }

    // Fungsi ini digunakan untuk mengembalikan jumlah item dalam daftar buku
    override fun getItemCount(): Int {
        return bookList.size  // Mengembalikan jumlah buku dalam daftar
    }

    // ViewHolder adalah kelas yang berfungsi untuk memegang referensi tampilan yang akan ditampilkan dalam RecyclerView
    // Untuk setiap item yang ada, ViewHolder akan menyimpan tampilan yang diperlukan untuk menampilkan data
    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)  // Menyimpan referensi ke ImageView untuk gambar sampul buku
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)  // Menyimpan referensi ke TextView untuk judul buku
        private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)  // Menyimpan referensi ke TextView untuk penulis buku
        private val bookPrice: TextView = itemView.findViewById(R.id.book_price)  // Menyimpan referensi ke TextView untuk harga buku
        private val bookSinopsis: TextView = itemView.findViewById(R.id.book_synopsis)  // Menyimpan referensi ke TextView untuk sinopsis buku

        // Fungsi untuk mengikat data buku dengan tampilan (view) yang ada
        fun bind(book: Book) {
            // Mengisi tampilan dengan data buku
            bookTitle.text = book.judul  // Menampilkan judul buku
            bookAuthor.text = book.penulis  // Menampilkan penulis buku
            bookPrice.text = "Rp ${book.harga}"  // Menampilkan harga buku dengan format "Rp"

            // Menampilkan sinopsis buku, tetapi jika sinopsis lebih panjang dari 100 karakter, akan dipotong
            bookSinopsis.text = if (book.sinopsis.length > 100) {
                "${book.sinopsis.substring(0, 100)}..." // Menampilkan potongan sinopsis
            } else {
                book.sinopsis  // Menampilkan seluruh sinopsis
            }

            // Memuat gambar sampul buku menggunakan Glide
            Glide.with(context)
                .load(book.cover)  // Memuat gambar dari URL yang terdapat di objek book
                .into(bookCover)  // Menampilkan gambar pada ImageView bookCover

            // Menambahkan listener pada itemView untuk melakukan navigasi ketika item diklik
            itemView.setOnClickListener {
                // Membuat Bundle untuk mengirim data buku ke fragment tujuan
                val bundle = Bundle().apply {
                    putParcelable("bookDetails", book)  // Mengirim objek Book ke fragment ReadFragment
                }
                // Menavigasi ke fragment ReadFragment dengan membawa data buku yang dikirimkan
                it.findNavController().navigate(R.id.readFragment, bundle)
            }
        }
    }
}
