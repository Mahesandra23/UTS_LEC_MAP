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
        // Inflate layout untuk fragment ini
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        // Mengatur Bottom Navigation untuk navigasi halaman admin
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.admin_bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_book -> {
                    // Sudah berada di halaman ini, tidak melakukan apa-apa
                    true
                }
                R.id.book_list -> {
                    // Navigasi ke halaman daftar buku
                    findNavController().navigate(R.id.action_adminFragment_to_bookListFragment)
                    true
                }
                else -> false
            }
        }

        // Mengatur TextView untuk navigasi ke halaman tambah buku
        val addBookTextView = view.findViewById<TextView>(R.id.add_book1)
        addBookTextView.setOnClickListener {
            // Navigasi ke halaman tambah buku
            findNavController().navigate(R.id.action_adminFragment_to_addBookFragment)
        }

        // Tombol Logout untuk keluar dari akun
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Logout dari Firebase Authentication
            FirebaseAuth.getInstance().signOut()

            // Navigasi ke halaman login setelah logout
            findNavController().navigate(R.id.action_adminFragment_to_loginFragment)
        }

        // Mengembalikan tampilan view fragment
        return view
    }
}
