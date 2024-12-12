package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    // Deklarasi elemen-elemen UI yang digunakan di fragment ini
    private lateinit var bookCoverImageView: ImageView
    private lateinit var bookTitleTextView: TextView
    private lateinit var bookPriceTextView: TextView
    private lateinit var spinnerPaymentMethod: Spinner
    private lateinit var checkoutButton: Button
    private lateinit var cancelButton: Button // Tombol untuk membatalkan pesanan

    // Inisialisasi Firebase untuk autentikasi dan referensi database
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val databaseRef = FirebaseDatabase.getInstance().reference

    // Fungsi untuk membuat tampilan fragment dan mengatur elemen UI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Menginflate layout untuk fragment ini
        val view = inflater.inflate(R.layout.fragment_payment, container, false)

        // Menghubungkan elemen-elemen UI dengan variabel
        bookCoverImageView = view.findViewById(R.id.iv_book_cover)
        bookTitleTextView = view.findViewById(R.id.tv_book_title)
        bookPriceTextView = view.findViewById(R.id.tv_book_price)
        spinnerPaymentMethod = view.findViewById(R.id.spinner_payment_method)
        checkoutButton = view.findViewById(R.id.btn_checkout)
        cancelButton = view.findViewById(R.id.btn_cancel_order) // Menghubungkan tombol cancel

        // Mengambil data dari argument yang diteruskan ke fragment ini
        val bookTitle = arguments?.getString("bookTitle") ?: "Unknown"
        val bookPrice = arguments?.getInt("bookPrice") ?: 0
        val bookCover = arguments?.getString("bookCover") ?: ""

        // Menampilkan data buku ke elemen UI yang sesuai
        bookTitleTextView.text = bookTitle
        bookPriceTextView.text = "Price: Rp. $bookPrice"
        Glide.with(requireContext()).load(bookCover).into(bookCoverImageView)

        // Menyiapkan adapter untuk spinner metode pembayaran
        val paymentMethods = arrayOf("Credit Card", "Bank Transfer", "E-Wallet")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            paymentMethods
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPaymentMethod.adapter = adapter

        // Menangani klik pada tombol checkout
        checkoutButton.setOnClickListener {
            val selectedPaymentMethod = spinnerPaymentMethod.selectedItem.toString()
            processPayment(bookTitle, bookPrice, selectedPaymentMethod)
        }

        // Menangani klik pada tombol cancel
        cancelButton.setOnClickListener {
            // Kembali ke fragment sebelumnya (misalnya, fragment sebelumnya adalah halaman daftar buku)
            activity?.onBackPressed()
        }

        return view
    }

    // Fungsi untuk memproses pembayaran dan menyimpan data transaksi ke Firebase
    private fun processPayment(bookTitle: String, amount: Int, paymentMethod: String) {
        // Membuat ID pembayaran unik menggunakan UUID
        val paymentId = UUID.randomUUID().toString()
        // Mendapatkan ID pengguna yang sedang login
        val userId = firebaseAuth.currentUser?.uid ?: return
        // Mendapatkan tanggal dan waktu transaksi
        val dateOfPurchase = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Menyusun data pembayaran yang akan disimpan ke Firebase
        val paymentData = mapOf(
            "paymentId" to paymentId,
            "userId" to userId,
            "judul" to bookTitle,
            "harga" to amount,
            "paymentMethod" to paymentMethod,
            "dateOfPurchase" to dateOfPurchase
        )

        // Menyimpan data pembayaran ke Firebase Realtime Database
        databaseRef.child("payments").child(paymentId).setValue(paymentData)
            .addOnSuccessListener {
                // Jika pembayaran berhasil, navigasi ke fragment sukses pembayaran
                val bundle = Bundle().apply {
                    putInt("bookPrice", amount)
                }
                findNavController().navigate(R.id.action_paymentFragment_to_successfulPaymentFragment, bundle)
            }
            .addOnFailureListener {
                // Menampilkan pesan error jika terjadi kegagalan pada penyimpanan data pembayaran
                Toast.makeText(requireContext(), "Payment failed. Try again.", Toast.LENGTH_SHORT).show()
            }
    }
}
