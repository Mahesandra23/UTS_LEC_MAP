package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.uts_lec_map.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    private lateinit var bookDetailsText: TextView
    private lateinit var paymentAmountText: TextView
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var checkoutButton: Button

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseRef = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        bookDetailsText = view.findViewById(R.id.bookDetails)
        paymentAmountText = view.findViewById(R.id.paymentAmount)
        spinnerPaymentMethod = view.findViewById(R.id.spinnerPaymentMethod)
        checkoutButton = view.findViewById(R.id.btnCheckout)

        val bookId = arguments?.getString("bookId") ?: ""
        val bookPrice = arguments?.getDouble("bookPrice") ?: 0.0
        paymentAmountText.text = "Amount: $$bookPrice"

        checkoutButton.setOnClickListener {
            processPayment(bookId, bookPrice)
        }

        return view
    }

    private fun processPayment(bookId: String, amount: Double) {
        val paymentId = UUID.randomUUID().toString()  // Generate a unique payment ID
        val userId = firebaseAuth.currentUser?.uid ?: return  // Get the current user's ID
        val paymentMethod = spinnerPaymentMethod.selectedItem.toString()
        val dateOfPurchase = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val paymentData = mapOf(
            "paymentId" to paymentId,
            "userId" to userId,
            "bookId" to bookId,
            "amount" to amount,
            "paymentMethod" to paymentMethod,
            "dateOfPurchase" to dateOfPurchase
        )

        databaseRef.child("payments").child(paymentId).setValue(paymentData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Payment successful!", Toast.LENGTH_SHORT).show()
                // Navigate to another screen or update UI
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Payment failed. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
