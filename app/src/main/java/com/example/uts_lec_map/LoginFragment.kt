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
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        val emailEditText = view.findViewById<EditText>(R.id.et_email_login)
        val passwordEditText = view.findViewById<EditText>(R.id.et_password_login)
        val signUpTextView = view.findViewById<TextView>(R.id.tv_sign_up)
        val fullText = "Don't have an account? Sign Up"
        val spannableString = SpannableString(fullText)

        auth = FirebaseAuth.getInstance()

        // Mengatur teks ke TextView
        signUpTextView.text = spannableString

        // Menentukan posisi dari "Sign Up"
        val start = fullText.indexOf("Sign Up")
        val end = start + "Sign Up".length

        // Mengatur warna teks
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

        // Fungsi untuk mengubah warna button berdasarkan status field
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

        // Validasi email
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isEmailValid(s.toString())) {
                    emailEditText.error = "Email must contain '@'"
                } else {
                    emailEditText.error = null
                }
                checkFields()
            }
        })

        // Validasi password
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkFields()
            }
        })

        // Initial state check for button color
        checkFields()

        // Handle ketika tombol login diklik
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val userRef = FirebaseDatabase.getInstance().getReference("users/$userId")

                            // Cek apakah user adalah admin
                            userRef.get().addOnSuccessListener { snapshot ->
                                val isAdmin = snapshot.child("isAdmin").getValue(Boolean::class.java) ?: false
                                if (isAdmin) {
                                    // Navigasi ke AdminFragment jika user adalah admin
                                    findNavController().navigate(R.id.action_loginFragment_to_adminFragment)
                                } else {
                                    // Navigasi ke HomeFragment jika user bukan admin
                                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                }
                            }.addOnFailureListener {
                                Toast.makeText(activity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(activity, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return view
    }

    // Fungsi validasi email
    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }
}