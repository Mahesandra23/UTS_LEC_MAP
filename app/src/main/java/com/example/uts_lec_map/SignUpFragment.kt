package com.example.uts_lec_map

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
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
        val signUpTextView = view.findViewById<TextView>(R.id.tv_login)
        val spannableString = SpannableString("Already have an account? Login")

        // Set warna biru untuk kata "Login"
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            spannableString.length - 5,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        signUpTextView.text = spannableString

        // Navigate to login page
        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // DatePicker untuk tanggal lahir
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

        // Handle tombol Sign Up
        signUpButton.setOnClickListener {
            val nama = namaEditText.text.toString()
            val tanggalLahir = tanggalLahirEditText.text.toString()
            val noTelp = noTelpEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Validasi input
            if (nama.isEmpty() || tanggalLahir.isEmpty() || noTelp.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(activity, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Simpan data user ke Firebase Realtime Database
                        val userId = auth.currentUser?.uid ?: ""
                        val userRef = database.getReference("users").child(userId)

                        val userMap = mapOf(
                            "nama" to nama,
                            "tanggal_lahir" to tanggalLahir,
                            "no_telp" to noTelp,
                            "email" to email
                        )

                        userRef.setValue(userMap).addOnCompleteListener { dataTask ->
                            if (dataTask.isSuccessful) {
                                Toast.makeText(activity, "Sign Up successful", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                            } else {
                                Toast.makeText(activity, "Failed to save user data: ${dataTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(activity, "Sign Up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}