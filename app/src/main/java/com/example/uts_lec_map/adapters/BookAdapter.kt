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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BookAdapter(private val context: Context, private val bookList: List<Book>) :
    RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val paymentDatabase: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("payments")
    private val userId: String = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

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
            bookPrice.text = if (book.harga == 0) "Gratis" else "Rp ${book.harga}"

            // Memuat gambar menggunakan Glide
            Glide.with(context)
                .load(book.cover)
                .into(bookCover)

            // Listener untuk navigasi berdasarkan status pembelian
            checkIfBookPurchased(book) { isPurchased ->
                itemView.setOnClickListener {
                    if (isPurchased) {
                        // Navigasi ke ReadFragment jika buku sudah dibeli
                        val bundle = Bundle().apply {
                            putParcelable("bookDetails", book)
                        }
                        it.findNavController().navigate(R.id.readFragment, bundle)
                    } else {
                        // Navigasi ke DetailBookFragment jika buku belum dibeli
                        val bundle = Bundle().apply {
                            putString("bookTitle", book.judul)
                        }
                        it.findNavController().navigate(R.id.detailBookFragment, bundle)
                    }
                }
            }
        }

        private fun checkIfBookPurchased(book: Book, callback: (Boolean) -> Unit) {
            paymentDatabase.orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val purchasedBooks = snapshot.children.mapNotNull {
                                it.child("judul").value as? String
                            }
                            callback(book.judul in purchasedBooks) // Buku sudah dibeli?
                        } else {
                            callback(false) // Tidak ada pembelian tercatat
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(false) // Asumsi gagal mengecek sebagai belum dibeli
                    }
                })
        }
    }
}
