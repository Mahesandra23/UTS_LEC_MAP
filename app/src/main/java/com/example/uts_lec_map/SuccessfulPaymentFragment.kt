package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

// Fragment ini akan menampilkan halaman pembayaran yang berhasil
class SuccessfulPaymentFragment : Fragment() {

    // Fungsi ini dipanggil ketika fragment ini dibuat dan menampilkan view-nya
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menginflate layout untuk fragment_successful_payment
        val view = inflater.inflate(R.layout.fragment_successful_payment, container, false)

        // Bind atau hubungkan elemen-elemen UI dengan variabel yang ada di dalam kode
        val successMessageTextView = view.findViewById<TextView>(R.id.tv_success_message)
        val thankyouMessageTextView = view.findViewById<TextView>(R.id.thankyou_message)
        val bookPriceTextView = view.findViewById<TextView>(R.id.tv_book_price)
        val backToHomeButton = view.findViewById<Button>(R.id.btn_back_to_home)

        // Mengambil argumen yang diteruskan ke fragment ini (dalam hal ini adalah harga buku)
        val bookPrice = arguments?.getInt("bookPrice") ?: 0

        // Menampilkan data yang telah diambil pada elemen-elemen UI yang sesuai
        successMessageTextView.text = "Payment Successful!"  // Menampilkan pesan sukses pembayaran
        thankyouMessageTextView.text = "Thank you for your purchase"  // Pesan ucapan terima kasih
        bookPriceTextView.text = "Total: Rp. $bookPrice"  // Menampilkan harga buku yang dibeli

        // Menangani klik pada tombol "Back to Home"
        backToHomeButton.setOnClickListener {
            // Navigasi kembali ke halaman HomeFragment
            findNavController().navigate(R.id.action_successfulPaymentFragment_to_homeFragment)
        }

        // Mengembalikan view untuk fragment ini
        return view
    }
}
