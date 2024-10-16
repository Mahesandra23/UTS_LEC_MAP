package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.core.content.res.ResourcesCompat

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Set up the title with Montserrat Bold font
        val loginTitle: TextView = view.findViewById(R.id.tv_login_title)
        loginTitle.typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_bold)

        val signUpTextView = view.findViewById<TextView>(R.id.tv_sign_up)
        signUpTextView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        val loginButton = view.findViewById<Button>(R.id.btn_login)
        loginButton.setOnClickListener {
            // Handle login logic here
            val email = view.findViewById<EditText>(R.id.et_email_login).text.toString()
            val password = view.findViewById<EditText>(R.id.et_password_login).text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Lakukan validasi login
                Toast.makeText(activity, "Login successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
