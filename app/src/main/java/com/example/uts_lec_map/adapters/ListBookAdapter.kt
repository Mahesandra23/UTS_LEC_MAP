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
// Adapter berfungsi menghubungkan data dengan tampilan di dalam RecyclerView
// Di sini, data yang digunakan adalah daftar buku (bookList) yang akan ditampilkan di setiap item
class ListBookAdapter(private val context: Context, private val bookList: List<Book>) :
    RecyclerView.Adapter<ListBookAdapter.ListBookViewHolder>() {

    // Fungsi ini dipanggil untuk membuat ViewHolder yang akan menampung tampilan untuk setiap item di RecyclerView
    // ViewHolder akan berisi tampilan yang didefinisikan dalam layout XML (book_item.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListBookViewHolder {
        // Menggunakan LayoutInflater untuk membuat tampilan item dari layout book_item.xml
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return ListBookViewHolder(view)  // Mengembalikan ListBookViewHolder yang berisi tampilan item buku
    }

    // Fungsi ini dipanggil untuk mengikat data (objek Book) ke tampilan (View) di ViewHolder
    // Data yang dikirimkan adalah objek buku berdasarkan posisi item dalam RecyclerView
    override fun onBindViewHolder(holder: ListBookViewHolder, position: Int) {
        val book = bookList[position]  // Mendapatkan data buku dari daftar berdasarkan posisi
        holder.bind(book)  // Mengikat data buku ke tampilan yang ada di ViewHolder
    }

    // Fungsi ini mengembalikan jumlah item dalam daftar buku
    // RecyclerView akan menampilkan jumlah item sesuai dengan nilai yang dikembalikan
    override fun getItemCount(): Int {
        return bookList.size  // Mengembalikan jumlah buku yang ada dalam daftar
    }

    // ViewHolder adalah kelas yang digunakan untuk menyimpan tampilan yang digunakan dalam setiap item di RecyclerView
    // Di sini, ViewHolder berfungsi menyimpan tampilan untuk setiap item buku
    inner class ListBookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)  // Menyimpan referensi ke ImageView untuk sampul buku
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)  // Menyimpan referensi ke TextView untuk judul buku
        private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)  // Menyimpan referensi ke TextView untuk penulis buku
        private val bookPrice: TextView = itemView.findViewById(R.id.book_price)  // Menyimpan referensi ke TextView untuk harga buku

        // Fungsi ini digunakan untuk mengikat data buku ke tampilan yang ada di ViewHolder
        // Data buku diambil dari parameter `book` dan kemudian ditampilkan pada masing-masing tampilan
        fun bind(book: Book) {
            // Mengisi tampilan dengan data dari objek book
            bookTitle.text = book.judul  // Menampilkan judul buku
            bookAuthor.text = book.penulis  // Menampilkan penulis buku
            bookPrice.text = "Rp ${book.harga}"  // Menampilkan harga buku dengan format "Rp"

            // Menggunakan Glide untuk memuat gambar sampul buku dari URL yang ada pada objek book
            Glide.with(context)
                .load(book.cover)  // Memuat gambar dari URL yang terdapat dalam objek book
                .into(bookCover)  // Menampilkan gambar pada ImageView bookCover

            // Menambahkan listener pada itemView untuk menangani aksi klik
            itemView.setOnClickListener {
                // Membuat Bundle untuk mengirimkan data buku (Book) ke fragment berikutnya
                val bundle = Bundle().apply {
                    putParcelable("bookData", book)  // Mengirimkan objek buku ke fragment tujuan
                }
                // Menavigasi ke EditBookFragment dengan membawa data buku yang dikirim melalui Bundle
                it.findNavController().navigate(R.id.action_listBookFragment_to_editBookFragment, bundle)
            }
        }
    }
}
