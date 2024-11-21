package com.example.uts_lec_map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.uts_lec_map.databinding.FragmentEditBookBinding
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.FirebaseDatabase

class EditBookFragment : Fragment() {
    private var _binding: FragmentEditBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the back button
        binding.ivBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Retrieve the book data passed from the previous fragment
        val book: Book? = arguments?.getParcelable("bookData")

        book?.let {
            // Populate the fields with the book's data
            binding.namaBuku.setText(it.judul)
            binding.penulisBuku.setText(it.penulis)
            binding.hargaBuku.setText(it.harga.toString())
            binding.sinopsis.setText(it.sinopsis)
            binding.ceritaBuku.setText(it.isi_cerita)

            Glide.with(this)
                .load(it.cover)
                .into(binding.imageView)
        }

        // Save button click listener
        binding.btnSaveEditedBook.setOnClickListener {
            val updatedBook = Book(
                id = book?.id ?: "",
                judul = binding.namaBuku.text.toString(),
                penulis = binding.penulisBuku.text.toString(),
                harga = binding.hargaBuku.text.toString().toIntOrNull() ?: 0,
                cover = book?.cover ?: "",
                sinopsis = binding.sinopsis.text.toString(),
                isi_cerita = binding.ceritaBuku.text.toString()
            )

            saveBookToFirebase(updatedBook)
        }

        // Delete button click listener
        binding.btnDeleteBook.setOnClickListener {
            book?.let { bookToDelete ->
                deleteBookFromFirebase(bookToDelete.id)
            } ?: run {
                Toast.makeText(requireContext(), "Book not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveBookToFirebase(book: Book) {
        val bookDatabase = FirebaseDatabase.getInstance().getReference("buku")
        val bookId = book.id

        val updatedBookData = mapOf(
            "id" to bookId,
            "judul" to book.judul,
            "penulis" to book.penulis,
            "harga" to book.harga,
            "cover" to book.cover,
            "sinopsis" to book.sinopsis,
            "isi_cerita" to book.isi_cerita
        )

        bookDatabase.child(bookId).setValue(updatedBookData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Book updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editBookFragment_to_listBookFragment)
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Failed to update book: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteBookFromFirebase(bookId: String) {
        val bookDatabase = FirebaseDatabase.getInstance().getReference("buku")
        bookDatabase.child(bookId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Book deleted successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editBookFragment_to_listBookFragment)
            }
            .addOnFailureListener { error ->
                Toast.makeText(requireContext(), "Failed to delete book: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
