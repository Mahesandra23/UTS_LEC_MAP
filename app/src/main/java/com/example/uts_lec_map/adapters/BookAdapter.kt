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

class BookAdapter(
    private val context: Context,
    private val bookList: List<Book>,
    private val onBookClick: (String, Boolean) -> Unit // Perbarui parameter onClick
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.bind(book)
    }

    override fun getItemCount(): Int = bookList.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        private val bookTitle: TextView = itemView.findViewById(R.id.book_title)
        private val bookAuthor: TextView = itemView.findViewById(R.id.book_author)
        private val bookPrice: TextView = itemView.findViewById(R.id.book_price)

        fun bind(book: Book) {
            bookTitle.text = book.judul
            bookAuthor.text = book.penulis
            bookPrice.text = "Rp ${book.harga}"

            Glide.with(context)
                .load(book.cover)
                .into(bookCover)

            itemView.setOnClickListener {
                onBookClick(book.judul, book.isPurchased) // Kirim judul dan status pembelian
            }
        }
    }
}
