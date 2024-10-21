package com.example.uts_lec_map

import android.app.DatePickerDialog
import android.graphics.Color
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
import java.util.Calendar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
const val ARG_PARAM1 = "param1"
const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        val spannableString = SpannableString("Already have an account? login")

        auth = FirebaseAuth.getInstance()


// Set warna biru untuk kata "login"
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            spannableString.length - 5,
            spannableString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        signUpTextView.text = spannableString

        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }


        // Fungsi untuk mengecek apakah ada input yang kosong
        fun isAnyFieldEmpty(): Boolean {
            return namaEditText.text.isEmpty() ||
                    tanggalLahirEditText.text.isEmpty() ||
                    noTelpEditText.text.isEmpty() ||
                    emailEditText.text.isEmpty() ||
                    passwordEditText.text.isEmpty() ||
                    confirmPasswordEditText.text.isEmpty()
        }

        val isAllFieldsFilled = namaEditText.text.isNotEmpty() &&
                tanggalLahirEditText.text.isNotEmpty() &&
                noTelpEditText.text.isNotEmpty() &&
                emailEditText.text.isNotEmpty() &&
                passwordEditText.text.isNotEmpty() &&
                confirmPasswordEditText.text.isNotEmpty()

        if (isAllFieldsFilled) {
            signUpButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.black) // Ubah warna ke warna aktif
        } else {
            signUpButton.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.grey) // Warna abu-abu
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
                                // Firebase sign-up success
                                Toast.makeText(activity, "Sign Up successful", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
                            } else {
                                // Firebase sign-up failed
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

        // Fungsi untuk menampilkan pesan validasi
        fun showValidationWarning(message: String) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
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

// Handle ketika tombol Sign Up diklik
        signUpButton.setOnClickListener {
            if (isAnyFieldEmpty()) {
                showFillAllFieldsWarning()
            } else {
                // Jika semua validasi sudah ditangani di TextWatcher, lanjutkan ke proses sign up
                Toast.makeText(activity, "Sign Up successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            }
        }





        return view
    }
}
