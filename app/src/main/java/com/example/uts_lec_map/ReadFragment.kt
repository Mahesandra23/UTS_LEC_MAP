package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.uts_lec_map.databinding.FragmentReadBinding

class ReadFragment : Fragment() {
    private var _binding: FragmentReadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadBinding.inflate(inflater, container, false)

        // Ambil data dari arguments
        val judul = arguments?.getString("judul") ?: "Unknown Title"
        val penulis = arguments?.getString("penulis") ?: "Unknown Author"
        val isi_cerita = arguments?.getString("isi_cerita") ?: "No Content Available"

        // Set data ke komponen UI
        binding.bookTitle.text = judul
        binding.bookAuthor.text = "By $penulis"
        binding.bookCerita.text = isi_cerita // Menampilkan isi cerita

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up back button click listener to navigate to Home Fragment
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_readFragment_to_homeFragment)  // Navigate to Home fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
