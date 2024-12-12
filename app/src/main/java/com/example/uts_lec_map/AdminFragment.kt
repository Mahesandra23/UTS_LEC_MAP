package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class AdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        // Set up bottom navigation
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_book -> {
                    // sudah di page ini
                    true
                }
                R.id.book_list -> {
                    findNavController().navigate(R.id.action_adminFragment_to_bookListFragment)
                    true
                }
                else -> false
            }
        }

        // Set up add book TextView
        val addBookTextView = view.findViewById<TextView>(R.id.add_book1)
        addBookTextView.setOnClickListener {
            findNavController().navigate(R.id.action_adminFragment_to_addBookFragment)
        }

        // Logout button logic
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Logout from Firebase
            FirebaseAuth.getInstance().signOut()

            // Navigate to login screen
            findNavController().navigate(R.id.action_adminFragment_to_loginFragment)
        }

        return view
    }
}
