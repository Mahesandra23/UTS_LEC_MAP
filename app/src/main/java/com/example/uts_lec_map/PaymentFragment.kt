package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    private lateinit var bookCoverImageView: ImageView
    private lateinit var bookTitleTextView: TextView
    private lateinit var bookPriceTextView: TextView
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var checkoutButton: Button

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        // Initialize views
        bookCoverImageView = view.findViewById(R.id.iv_book_cover)
        bookTitleTextView = view.findViewById(R.id.tv_book_title)
        bookPriceTextView = view.findViewById(R.id.tv_book_price)
        spinnerPaymentMethod = view.findViewById(R.id.spinner_payment_method)
        checkoutButton = view.findViewById(R.id.btn_checkout)

        // Get arguments
        val bookTitle = arguments?.getString("bookTitle") ?: "Unknown"
        val bookPrice = arguments?.getInt("bookPrice") ?: 0
        val bookCover = arguments?.getString("bookCover") ?: ""

        // Set data to views
        bookTitleTextView.text = bookTitle
        bookPriceTextView.text = "Price: Rp. $bookPrice"
        Glide.with(requireContext()).load(bookCover).into(bookCoverImageView)

        // Setup spinner
        val paymentMethods = arrayOf("Credit Card", "Bank Transfer", "E-Wallet")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            paymentMethods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = adapter

        // Checkout button click
        checkoutButton.setOnClickListener {
            val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()
            processPayment(bookTitle, bookPrice, selectedPaymentMethod)
        }

        return view
    }

    private fun processPayment(bookTitle: String, amount: Int, paymentMethod: String) {
        val paymentId = UUID.randomUUID().toString()
        val userId = firebaseAuth.currentUser?.uid ?: return
        val dateOfPurchase = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val paymentData = mapOf(
            "paymentId" to paymentId,
            "userId" to userId,
            "judul" to bookTitle,
            "harga" to amount,
            "paymentMethod" to paymentMethod,
            "dateOfPurchase" to dateOfPurchase
        )

        databaseRef.child("payments").child(paymentId).setValue(paymentData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Payment successful!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Payment failed. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
