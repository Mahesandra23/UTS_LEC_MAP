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
    private lateinit var auth: FirebaseAuth // Deklarasi objek FirebaseAuth untuk otentikasi pengguna
    private lateinit var database: FirebaseDatabase // Deklarasi objek FirebaseDatabase untuk akses ke Realtime Database

    // Fungsi untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        // Mendeklarasikan elemen tampilan (Button, EditText, TextView) dari layout
        val signUpButton = view.findViewById<Button>(R.id.btn_signup)
        val namaEditText = view.findViewById<EditText>(R.id.nama)
        val tanggalLahirEditText = view.findViewById<EditText>(R.id.tanggal_lahir)
        val noTelpEditText = view.findViewById<EditText>(R.id.no_telp)
        val emailEditText = view.findViewById<EditText>(R.id.et_email_signup)
        val passwordEditText = view.findViewById<EditText>(R.id.et_password_signup)
        val confirmPasswordEditText = view.findViewById<EditText>(R.id.et_confirm_password_signup)
        val signUpTextView: TextView = view.findViewById(R.id.signUpTextView)

        // Inisialisasi FirebaseAuth dan FirebaseDatabase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Fungsi untuk mengecek apakah ada field yang kosong
        fun isAnyFieldEmpty(): Boolean {
            return namaEditText.text.isEmpty() ||
                    tanggalLahirEditText.text.isEmpty() ||
                    noTelpEditText.text.isEmpty() ||
                    emailEditText.text.isEmpty() ||
                    passwordEditText.text.isEmpty() ||
                    confirmPasswordEditText.text.isEmpty()
        }

        // Fungsi untuk menampilkan peringatan jika ada field yang kosong
        fun showFillAllFieldsWarning() {
            Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }

        // Menangani klik pada tombol Sign Up
        signUpButton.setOnClickListener {
            if (isAnyFieldEmpty()) {
                showFillAllFieldsWarning() // Menampilkan pesan peringatan jika ada field kosong
            } else {
                val email = emailEditText.text.toString() // Mengambil email dari EditText
                val password = passwordEditText.text.toString() // Mengambil password dari EditText
                val confirmPassword = confirmPasswordEditText.text.toString() // Mengambil konfirmasi password

                // Mengecek apakah password dan konfirmasi password sama
                if (password == confirmPassword) {
                    // Mencoba untuk membuat akun baru menggunakan Firebase Authentication
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val userId = auth.currentUser?.uid // Mendapatkan ID pengguna saat ini

                                // Membuat map untuk menyimpan data pengguna ke Firebase Realtime Database
                                val userMap = hashMapOf(
                                    "name" to namaEditText.text.toString(),
                                    "dateOfBirth" to tanggalLahirEditText.text.toString(),
                                    "phone" to noTelpEditText.text.toString(),
                                    "email" to email
                                )

                                // Menyimpan data pengguna pada Realtime Database
                                userId?.let {
                                    database.getReference("users").child(it).setValue(userMap)
                                        .addOnCompleteListener { databaseTask ->
                                            if (databaseTask.isSuccessful) {
                                                // Menampilkan pesan sukses dan berpindah ke login
                                                Toast.makeText(activity, "Sign Up successful", Toast.LENGTH_SHORT).show()
                                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                                            } else {
                                                // Menampilkan pesan error jika gagal menyimpan data ke database
                                                Toast.makeText(activity, "Database Error: ${databaseTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            } else {
                                // Menampilkan pesan error jika proses sign up gagal
                                Toast.makeText(activity, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    // Menampilkan pesan error jika password tidak cocok dengan konfirmasi
                    Toast.makeText(activity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Menangani klik pada EditText untuk memilih tanggal lahir menggunakan DatePicker
        tanggalLahirEditText.setOnClickListener {
            val calendar = Calendar.getInstance() // Mengambil tanggal saat ini
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Membuka DatePicker untuk memilih tanggal lahir
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear" // Format tanggal
                    tanggalLahirEditText.setText(selectedDate) // Mengatur teks tanggal lahir
                },
                year, month, day
            )
            datePickerDialog.show() // Menampilkan dialog DatePicker
        }

        // Menangani klik pada teks untuk berpindah ke halaman login
        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        // Fungsi validasi untuk email
        fun isEmailValid(email: String): Boolean {
            return email.contains("@") // Memastikan email mengandung simbol '@'
        }

        // Fungsi validasi untuk nomor telepon
        fun isPhoneNumberValid(phone: String): Boolean {
            return phone.length >= 10 && phone.all { it.isDigit() } // Memastikan nomor telepon valid
        }

        // Fungsi validasi untuk password
        fun isPasswordValid(password: String): Boolean {
            return password.length >= 7 // Memastikan panjang password minimal 7 karakter
        }

        // Menambahkan TextWatcher untuk validasi nama pengguna
        namaEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Menampilkan error jika nama kosong
                if (s.isNullOrEmpty()) {
                    namaEditText.error = "Name cannot be empty"
                } else {
                    namaEditText.error = null
                }
            }
        })

        // Menambahkan TextWatcher untuk validasi email
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

        // Menambahkan TextWatcher untuk validasi nomor telepon
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

        // Menambahkan TextWatcher untuk validasi password
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

        // Menambahkan TextWatcher untuk validasi konfirmasi password
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

        return view // Mengembalikan tampilan fragment
    }
}
