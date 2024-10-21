package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.uts_lec_map.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isEditing = false // Track if the user is in edit mode
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        userId = auth.currentUser?.uid.toString()

        // Set up Bottom Navigation
        setupBottomNavigation()

        // Load user profile data from Firebase
        loadUserProfile()

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

    private fun loadUserProfile() {
        // Get user data from Firebase
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val phone = snapshot.child("phone").value.toString()
                    val dateOfBirth = snapshot.child("dateOfBirth").value.toString()

                    // Set user data to views
                    binding.usernameEditText.setText(name)
                    binding.emailEditText.setText(email)
                    binding.phoneEditText.setText(phone)
                } else {
                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupEditProfileButton() {
        binding.editProfileButton.setOnClickListener {
            isEditing = !isEditing // Toggle editing mode

            if (isEditing) {
                // Change to edit mode
                enableEditing(true)
                binding.saveButton.visibility = View.VISIBLE
                binding.editProfileButton.text = "Cancel"
            } else {
                // Change to view mode
                enableEditing(false)
                binding.saveButton.visibility = View.GONE
                binding.editProfileButton.text = "Edit Profile"
            }
        }

        // Save button functionality
        binding.saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun enableEditing(enable: Boolean) {
        binding.usernameEditText.isEnabled = enable
        binding.emailEditText.isEnabled = enable
        binding.phoneEditText.isEnabled = enable
    }

    private fun saveUserProfile() {
        val name = binding.usernameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()

        // Update user data in Firebase
        val userUpdates = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
        )

        database.child(userId).updateChildren(userUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                isEditing = false
                enableEditing(false)
                binding.saveButton.visibility = View.GONE
                binding.editProfileButton.text = "Edit Profile"
            } else {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
