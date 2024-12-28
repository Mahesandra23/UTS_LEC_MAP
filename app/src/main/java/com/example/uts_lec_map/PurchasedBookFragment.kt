package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.wallet.PaymentData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class PurchasedBookFragment : Fragment() {

    private lateinit var userIdToNameMap: Map<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchased_book, container, false)
        val tableLayout = view.findViewById<TableLayout>(R.id.tableLayout)
        val totalIncomeTextView = view.findViewById<TextView>(R.id.total_income)

        val database = FirebaseDatabase.getInstance()
        val usersRef = database.getReference("users")
        val paymentsRef = database.getReference("payments")

        usersRef.get().addOnSuccessListener { usersSnapshot ->
            userIdToNameMap = usersSnapshot.children.associate { user ->
                val userId = user.key ?: return@associate "" to "N/A"
                val name = user.child("name").getValue(String::class.java) ?: "N/A"
                userId to name
            }

            paymentsRef.get().addOnSuccessListener { paymentsSnapshot ->
                var totalIncome = 0

                // Add header row
                val headerRow = TableRow(context).apply {
                    addView(createHeaderTextView("Nama Pembeli"))
                    addView(createHeaderTextView("ID Pembeli"))
                    addView(createHeaderTextView("Judul Buku"))
                    addView(createHeaderTextView("Tanggal Pembelian"))
                    addView(createHeaderTextView("Harga"))
                }
                tableLayout.addView(headerRow)

                paymentsSnapshot.children.forEach { payment ->
                    val paymentData = payment.value as? Map<*, *> ?: return@forEach

                    val userId = paymentData["userId"] as? String ?: "N/A"
                    val bookTitle = paymentData["judul"] as? String ?: "N/A"
                    val price = (paymentData["harga"] as? Long)?.toInt() ?: 0
                    val dateOfPurchase = paymentData["dateOfPurchase"] as? String ?: "N/A"

                    totalIncome += price

                    val buyerName = userIdToNameMap[userId] ?: "Tidak diketahui"

                    val row = TableRow(context).apply {
                        addView(createDataTextView(buyerName))
                        addView(createDataTextView(userId))
                        addView(createDataTextView(bookTitle))
                        addView(createDataTextView(dateOfPurchase))
                        addView(createDataTextView(formatPrice(price)))
                    }

                    // Set margin to ensure distance between rows
                    val params = TableRow.LayoutParams().apply {
                        topMargin = 4 // Margin untuk jarak antar baris
                        bottomMargin = 4 // Margin untuk jarak antar baris
                    }
                    row.layoutParams = params

                    tableLayout.addView(row)
                }

                totalIncomeTextView.text = "Total Penghasilan: ${formatPrice(totalIncome)}"
            }
        }

        return view
    }

    private fun createHeaderTextView(content: String): TextView {
        return TextView(context).apply {
            text = content
            setPadding(16, 8, 16, 8)
            gravity = android.view.Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
            background = resources.getDrawable(R.drawable.table_header_background, null)
            typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)
        }
    }

    private fun createDataTextView(content: String): TextView {
        return TextView(context).apply {
            text = content
            setPadding(8, 10, 8, 8) // Padding untuk jarak antar kolom
            gravity = android.view.Gravity.CENTER
            typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        }
    }

    private fun addRowToTable(paymentData: Map<*, *>, tableLayout: TableLayout) {
        val row = TableRow(context).apply {
            // Margin antar baris
            val params = TableRow.LayoutParams().apply {
                topMargin = 20  // Menambahkan jarak atas antar baris
                bottomMargin = 20 // Menambahkan jarak bawah antar baris
            }
            layoutParams = params

            val userId = paymentData["userId"] as? String ?: "N/A"
            val bookTitle = paymentData["judul"] as? String ?: "N/A"
            val price = (paymentData["harga"] as? Long)?.toInt() ?: 0
            val dateOfPurchase = paymentData["dateOfPurchase"] as? String ?: "N/A"

            addView(createDataTextView(userId)) // Kolom 1
            addView(createDataTextView(bookTitle)) // Kolom 2
            addView(createDataTextView(formatPrice(price))) // Kolom 3
            addView(createDataTextView(dateOfPurchase)) // Kolom 4
        }

        tableLayout.addView(row)
    }

    private fun formatPrice(price: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(price)
    }
}
