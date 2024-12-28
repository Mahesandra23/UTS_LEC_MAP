package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class PurchasedBookFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchased_book, container, false)

        // Referensi ke elemen layout
        val tableLayout = view.findViewById<TableLayout>(R.id.purchased_books_table)
        val totalIncomeTextView = view.findViewById<TextView>(R.id.total_income)

        val database = FirebaseDatabase.getInstance()
        val purchasedBooksRef = database.getReference("purchasedBooks")

        // Fetch data dari Firebase Realtime Database
        purchasedBooksRef.get().addOnSuccessListener { dataSnapshot ->
            var totalIncome = 0

            dataSnapshot.children.forEach { child ->
                val buyerName = child.child("buyer_name").getValue(String::class.java) ?: "N/A"
                val buyerId = child.child("buyer_id").getValue(String::class.java) ?: "N/A"
                val date = child.child("purchase_date").getValue(String::class.java) ?: "N/A"
                val bookTitle = child.child("book_title").getValue(String::class.java) ?: "N/A"
                val price = child.child("price").getValue(Int::class.java) ?: 0

                totalIncome += price

                // Tambahkan baris ke tabel
                val row = TableRow(context).apply {
                    addView(createTextView(buyerName))
                    addView(createTextView(buyerId))
                    addView(createTextView(date))
                    addView(createTextView(bookTitle))
                    addView(createTextView(formatPrice(price)))
                }
                tableLayout.addView(row)
            }

            // Set total penghasilan
            totalIncomeTextView.text = "Total Penghasilan: ${formatPrice(totalIncome)}"
        }.addOnFailureListener {
            // Handle error saat fetch data
            totalIncomeTextView.text = "Gagal memuat data pembelian."
        }

        return view
    }

    private fun createTextView(content: String): TextView {
        return TextView(context).apply {
            text = content
            setPadding(8, 8, 8, 8)
            gravity = android.view.Gravity.CENTER
        }
    }

    private fun formatPrice(price: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(price)
    }
}
