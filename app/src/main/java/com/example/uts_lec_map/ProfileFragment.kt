package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.uts_lec_map.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isEditing = false // Track if the user is in edit mode

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Firebase instances
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Set up Bottom Navigation
        setupBottomNavigation()

        // Set up Edit Profile Button functionality
        setupEditProfileButton()

        return binding.root
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.profile

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    findNavController().navigate(R.id.action_profileFragment_to_historyFragment)
                    true
                }
                R.id.profile -> true
                else -> false
            }
        }
    }

    private fun setupEditProfileButton() {
        binding.editProfileButton.setOnClickListener {
            isEditing = !isEditing // Toggle editing mode

            if (isEditing) {
                // Change to edit mode
                binding.usernameEditText.isEnabled = true
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true
                binding.phoneEditText.isEnabled = true
                binding.saveButton.visibility = View.VISIBLE
                binding.editProfileButton.text = "Cancel"
            } else {
                // Change to view mode
                binding.usernameEditText.isEnabled = false
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
                binding.phoneEditText.isEnabled = false
                binding.saveButton.visibility = View.GONE
                binding.editProfileButton.text = "Edit Profile"
            }
        }

        // Save button functionality (saving profile data to Firebase)
        binding.saveButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()

            // Validasi input
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                Toast.makeText(activity, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simpan data ke Firebase
            val userId = auth.currentUser?.uid

            if (userId != null) {
                val userRef = database.getReference("users").child(userId)
                val userMap = mapOf(
                    "username" to username,
                    "email" to email,
                    "password" to password,
                    "phone" to phone
                )

                userRef.updateChildren(userMap).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        // Disable editing mode after saving
                        isEditing = false
                        setupEditProfileButton()
                    } else {
                        Toast.makeText(activity, "Failed to update profile: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(activity, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}