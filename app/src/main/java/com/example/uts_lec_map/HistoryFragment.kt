package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.uts_lec_map.databinding.FragmentHistoryBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        // Set up Bottom Navigation
        setupBottomNavigation()

        return binding.root
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.history // Mark History as selected

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_historyFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    findNavController().navigate(R.id.action_historyFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    // Already in HistoryFragment
                    true
                }
                R.id.profile -> {
                    findNavController().navigate(R.id.action_historyFragment_to_profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}