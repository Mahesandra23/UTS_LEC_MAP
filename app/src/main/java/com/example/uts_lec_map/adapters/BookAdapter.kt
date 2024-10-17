package com.example.uts_lec_map.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.uts_lec_map.R
import com.example.uts_lec_map.models.Book

class BookAdapter(
    private val context: Context,
    private val bookList: List<Book>,
    private val onBookClickListener: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        holder.bookTitle.text = book.title
        holder.bookCover.setImageResource(book.coverImage)

        // Set click listener to navigate when book is clicked
        holder.itemView.setOnClickListener {
            onBookClickListener(book)
        }
    }

    override fun getItemCount(): Int {
        return bookList.size
    }

    fun updateBooks(filteredBooks: List<Book>) {
        // You can implement this to update the book list dynamically
    }

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bookCover: ImageView = itemView.findViewById(R.id.book_cover)
        val bookTitle: TextView = itemView.findViewById(R.id.book_title)
    }
}
