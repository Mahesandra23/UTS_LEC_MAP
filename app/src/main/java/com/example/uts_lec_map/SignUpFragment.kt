package com.example.uts_lec_map

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class SignUpFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val signUpButton = view.findViewById<Button>(R.id.btn_signup)
        val namaEditText = view.findViewById<EditText>(R.id.nama)
        val tanggalLahirEditText = view.findViewById<EditText>(R.id.tanggal_lahir)
        val noTelpEditText = view.findViewById<EditText>(R.id.no_telp)
        val emailEditText = view.findViewById<EditText>(R.id.et_email_signup)
        val passwordEditText = view.findViewById<EditText>(R.id.et_password_signup)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.et_confirm_password_signup)
        val signUpTextView: TextView = view.findViewById(R.id.signUpTextView)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Fungsi untuk mengecek apakah ada input yang kosong
        fun isAnyFieldEmpty(): Boolean {
            return namaEditText.text.isEmpty() ||
                    tanggalLahirEditText.text.isEmpty() ||
                    noTelpEditText.text.isEmpty() ||
                    emailEditText.text.isEmpty() ||
                    passwordEditText.text.isEmpty() ||
                    confirmPasswordEditText.text.isEmpty()
        }

        // Fungsi untuk menampilkan pesan jika ada field yang kosong
        fun showFillAllFieldsWarning() {
            Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }

        // Handle ketika tombol Sign Up diklik
        signUpButton.setOnClickListener {
            if (isAnyFieldEmpty()) {
                showFillAllFieldsWarning()
            } else {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid

                                // Simpan data pengguna ke Firebase Realtime Database
                                val userMap = hashMapOf(
                                    "name" to namaEditText.text.toString(),
                                    "dateOfBirth" to tanggalLahirEditText.text.toString(),
                                    "phone" to noTelpEditText.text.toString(),
                                    "email" to email
                                )

                                userId?.let {
                                    database.getReference("users").child(it).setValue(userMap)
                                        .addOnCompleteListener { databaseTask ->
                                            if (databaseTask.isSuccessful) {
                                                Toast.makeText(activity, "Sign Up successful", Toast.LENGTH_SHORT).show()
                                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                                            } else {
                                                Toast.makeText(activity, "Database Error: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            } else {
                                Toast.makeText(activity, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    Toast.makeText(activity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Handle DatePicker untuk tanggal lahir
        tanggalLahirEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    tanggalLahirEditText.setText(selectedDate)
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // Fungsi untuk validasi email
        fun isEmailValid(email: String): Boolean {
            return email.contains("@")
        }

        // Fungsi untuk validasi nomor telepon
        fun isPhoneNumberValid(phone: String): Boolean {
            return phone.length >= 10 && phone.all { it.isDigit() }
        }

        // Fungsi untuk validasi password
        fun isPasswordValid(password: String): Boolean {
            return password.length >= 7
        }

        // Menambahkan TextWatcher untuk validasi langsung
        namaEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    namaEditText.error = "Name cannot be empty"
                } else {
                    namaEditText.error = null
                }
            }
        })

        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isEmailValid(s.toString())) {
                    emailEditText.error = "Email must contain '@'"
                } else {
                    emailEditText.error = null
                }
            }
        })

        noTelpEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isPhoneNumberValid(s.toString())) {
                    noTelpEditText.error = "Phone number must be at least 10 digits"
                } else {
                    noTelpEditText.error = null
                }
            }
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!isPasswordValid(s.toString())) {
                    passwordEditText.error = "Password must be at least 7 characters"
                } else {
                    passwordEditText.error = null
                }
            }
        })

        confirmPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != passwordEditText.text.toString()) {
                    confirmPasswordEditText.error = "Passwords do not match"
                } else {
                    confirmPasswordEditText.error = null
                }
            }
        })

        return view
    }
}
