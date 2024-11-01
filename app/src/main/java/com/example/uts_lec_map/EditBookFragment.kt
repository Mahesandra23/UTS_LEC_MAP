package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class EditBookFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur click listener untuk tombol back
        view.findViewById<ImageView>(R.id.iv_back_button).setOnClickListener {
            findNavController().popBackStack()  // Navigasi kembali ke fragment sebelumnya
        }

        // Tombol simpan buku
        view.findViewById<Button>(R.id.btn_save_edited_book).setOnClickListener {
            findNavController().navigate(R.id.action_editBookFragment_to_adminFragment)
        }
    }
}
