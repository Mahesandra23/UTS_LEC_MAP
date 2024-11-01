package com.example.uts_lec_map

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class AdminFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

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

        // Menambahkan listener untuk TextView
        val addBookTextView = view.findViewById<TextView>(R.id.add_book1)

        addBookTextView.setOnClickListener {
            // Navigasi ke fragment atau activity untuk menambah buku
            findNavController().navigate(R.id.action_adminFragment_to_addBookFragment) // ganti sesuai dengan ID action yang sesuai
        }

        return view
    }
}
