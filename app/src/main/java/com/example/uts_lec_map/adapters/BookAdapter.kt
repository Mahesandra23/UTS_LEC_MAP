package com.example.uts_lec_map.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.uts_lec_map.R
import com.example.uts_lec_map.models.Book

class BookAdapter(private val context: Context, private val bookList: List<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.bookTitle.text = book.judul
        holder.bookAuthor.text = book.penulis
        holder.bookPrice.text = "Rp ${book.harga}"
        // Menggunakan Glide untuk memuat gambar dari URL
        Glide.with(context)
            .load(book.cover) // URL gambar dari Firebase
            .into(holder.bookCover)
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
        val bookPrice: TextView = itemView.findViewById(R.id.book_price)
    }
}
