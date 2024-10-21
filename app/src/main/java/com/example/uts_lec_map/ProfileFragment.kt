
package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.uts_lec_map.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isEditing = false // Track if the user is in edit mode

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

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

        // Save button functionality (you can add saving logic here)
        binding.saveButton.setOnClickListener {
            // Implement save logic (e.g., update user data in Firebase)
            isEditing = false
            setupEditProfileButton()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}