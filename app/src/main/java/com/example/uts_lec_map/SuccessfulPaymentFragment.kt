package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class SuccessfulPaymentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_successful_payment, container, false)

        // Bind views
        val successMessageTextView = view.findViewById<TextView>(R.id.tv_success_message)
        val thankyouMessageTextView = view.findViewById<TextView>(R.id.thankyou_message)
        val bookPriceTextView = view.findViewById<TextView>(R.id.tv_book_price)
        val backToHomeButton = view.findViewById<Button>(R.id.btn_back_to_home)

        // Get arguments
        val bookPrice = arguments?.getInt("bookPrice") ?: 0

        // Set data to views
        successMessageTextView.text = "Payment Successful!"
        thankyouMessageTextView.text = "Thank you for your purchase"
        bookPriceTextView.text = "Total: Rp. $bookPrice"

        // Back to home button click
        backToHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_successfulPaymentFragment_to_homeFragment)
        }

        return view
    }
}
