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

class BookForReadAdapter(private val context: Context, private val bookList: List<Book>) :
    RecyclerView.Adapter<BookForReadAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
        private val bookPrice: TextView = itemView.findViewById(R.id.book_price)

        fun bind(book: Book) {
            bookTitle.text = book.judul
            bookAuthor.text = book.penulis
            bookPrice.text = "Rp ${book.harga}"

            // Memuat gambar menggunakan Glide
            Glide.with(context)
                .load(book.cover)
                .into(bookCover)

            // Menambahkan listener pada itemView untuk navigasi ke ReadFragment
            itemView.setOnClickListener {
                // Mengirim data buku ke ReadFragment
                val bundle = Bundle().apply {
                    putParcelable("bookDetails", book) // Mengirim objek Book ke ReadFragment
                }
                it.findNavController().navigate(R.id.readFragment, bundle)
            }
        }
    }
}
