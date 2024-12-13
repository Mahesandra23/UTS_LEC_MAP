package com.example.uts_lec_map

import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginFragment : Fragment() {

    // Inisialisasi variabel untuk autentikasi Firebase
    private lateinit var auth: FirebaseAuth

    // Fungsi untuk membuat tampilan fragment dan mengatur logika UI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Menginflate layout fragment_login dan mengembalikan view-nya
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Menghubungkan elemen UI dengan variabel
        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val emailEditText = view.findViewById<EditText>(R.id.et_email_login)
        val passwordEditText = view.findViewById<EditText>(R.id.et_password_login)
        val signUpTextView = view.findViewById<TextView>(R.id.tv_sign_up)
        val fullText = "Don't have an account? Sign Up"
        val spannableString = SpannableString(fullText)

        // Inisialisasi FirebaseAuth untuk otentikasi pengguna
        auth = FirebaseAuth.getInstance()

        // Mengatur teks yang dapat dipilih di TextView "Sign Up"
        signUpTextView.text = spannableString

        // Menentukan posisi kata "Sign Up" dalam teks
        val start = fullText.indexOf("Sign Up")
        val end = start + "Sign Up".length

        // Mengatur warna teks "Sign Up" menjadi biru
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.blue)),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Fungsi untuk mengecek apakah ada field yang kosong
        fun isAnyFieldEmpty(): Boolean {
            return emailEditText.text.isEmpty() || passwordEditText.text.isEmpty()
        }

        // Fungsi untuk mengubah warna button dan status aktif berdasarkan apakah field kosong
        fun checkFields() {
            if (isAnyFieldEmpty()) {
                loginButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey)
                loginButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                loginButton.isEnabled = false
            } else {
                loginButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.black)
                loginButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                loginButton.isEnabled = true
            }
        }

        // Validasi input email
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Menampilkan error jika email tidak valid (tidak mengandung '@')
                if (!isEmailValid(s.toString())) {
                    emailEditText.error = "Email must contain '@'"
                } else {
                    emailEditText.error = null
                }
                // Memeriksa kembali status tombol login setelah perubahan teks
                checkFields()
            }
        })

        // Validasi input password
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Memeriksa kembali status tombol login setelah perubahan teks
                checkFields()
            }
        })

        // Initial state check untuk warna tombol login
        checkFields()

        // Mengatur listener untuk menangani klik pada tombol login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Melakukan login menggunakan email dan password
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Mendapatkan ID pengguna yang sedang login
                            val userId = auth.currentUser?.uid
                            val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")

                            // Mengecek apakah pengguna adalah admin
                            userRef.get().addOnSuccessListener { snapshot ->
                                val isAdmin = snapshot.child("isAdmin").getValue(Boolean::class.java) ?: false
                                if (isAdmin) {
                                    // Jika pengguna adalah admin, navigasi ke AdminFragment
                                    findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                                } else {
                                    // Jika bukan admin, navigasi ke HomeFragment
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                }
                            }.addOnFailureListener {
                                // Menampilkan toast jika gagal mengambil data pengguna
                                Toast.makeText(activity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            // Menampilkan toast jika login gagal
                            Toast.makeText(activity, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                // Menampilkan toast jika ada field yang kosong
                Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Menangani klik pada TextView untuk navigasi ke halaman Sign Up
        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return view
    }

    // Fungsi untuk memvalidasi format email (memeriksa keberadaan '@')
    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }
}
