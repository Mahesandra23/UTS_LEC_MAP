package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController

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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        val signUpButton = view.findViewById<Button>(R.id.btn_signup)
        signUpButton.setOnClickListener {
            // Handle sign-up logic here
            val email = view.findViewById<EditText>(R.id.et_email_signup).text.toString()
            val password = view.findViewById<EditText>(R.id.et_password_signup).text.toString()
            val confirmPassword = view.findViewById<EditText>(R.id.et_confirm_password_signup).text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && password == confirmPassword) {
                // Lakukan proses sign up
                Toast.makeText(activity, "Sign Up successful", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
            } else {
                Toast.makeText(activity, "Please fill out all fields or check password match", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
