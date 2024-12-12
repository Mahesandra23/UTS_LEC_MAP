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
    // Binding untuk mengakses elemen-elemen UI pada layout fragment_edit_book
    private var _binding: FragmentEditBookBinding? = null
    private val binding get() = _binding!!

    // Fungsi untuk membuat tampilan fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menginflate layout fragment_edit_book dan mengembalikan root view
        _binding = FragmentEditBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Fungsi yang dipanggil setelah tampilan fragment siap, digunakan untuk mengatur logika aplikasi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Mengatur tombol kembali untuk navigasi ke fragment sebelumnya
        binding.ivBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        // Mengambil data buku yang dikirim dari fragment sebelumnya
        val book: Book? = arguments?.getParcelable("bookData")

        // Jika data buku ada, maka data buku tersebut akan ditampilkan di UI
        book?.let {
            // Mengisi kolom input dengan data buku
            binding.namaBuku.setText(it.judul)
            binding.penulisBuku.setText(it.penulis)
            binding.hargaBuku.setText(it.harga.toString())
            binding.sinopsis.setText(it.sinopsis)
            binding.ceritaBuku.setText(it.isi_cerita)

            // Memuat gambar sampul buku menggunakan Glide
            Glide.with(this)
                .load(it.cover)
                .into(binding.imageView)
        }

        // Listener untuk tombol simpan setelah melakukan perubahan data buku
        binding.btnSaveEditedBook.setOnClickListener {
            // Membuat objek buku baru dengan data yang sudah diubah
            val updatedBook = Book(
                id = book?.id ?: "",  // Jika id buku kosong, set dengan string kosong
                judul = binding.namaBuku.text.toString(),
                penulis = binding.penulisBuku.text.toString(),
                harga = binding.hargaBuku.text.toString().toIntOrNull() ?: 0,  // Jika harga tidak valid, set ke 0
                cover = book?.cover ?: "",
                sinopsis = binding.sinopsis.text.toString(),
                isi_cerita = binding.ceritaBuku.text.toString()
            )

            // Mengirim data buku yang telah diperbarui ke Firebase
            saveBookToFirebase(updatedBook)
        }

        // Listener untuk tombol hapus buku
        binding.btnDeleteBook.setOnClickListener {
            book?.let { bookToDelete ->
                // Jika buku ada, hapus buku tersebut dari Firebase
                deleteBookFromFirebase(bookToDelete.id)
            } ?: run {
                // Jika buku tidak ditemukan, tampilkan pesan error
                Toast.makeText(requireContext(), "Book not found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan data buku yang diperbarui ke Firebase
    private fun saveBookToFirebase(book: Book) {
        // Referensi ke node "buku" pada Firebase Database
        val bookDatabase = FirebaseDatabase.getInstance().getReference("buku")
        val bookId = book.id  // ID buku yang akan diperbarui

        // Membuat map untuk data buku yang akan diperbarui
        val updatedBookData = mapOf(
            "id" to bookId,
            "judul" to book.judul,
            "penulis" to book.penulis,
            "harga" to book.harga,
            "cover" to book.cover,
            "sinopsis" to book.sinopsis,
            "isi_cerita" to book.isi_cerita
        )

        // Mengupdate data buku pada Firebase Database
        bookDatabase.child(bookId).setValue(updatedBookData)
            .addOnSuccessListener {
                // Jika update berhasil, tampilkan pesan sukses dan navigasi ke listBookFragment
                Toast.makeText(requireContext(), "Book updated successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editBookFragment_to_listBookFragment)
            }
            .addOnFailureListener { error ->
                // Jika update gagal, tampilkan pesan error
                Toast.makeText(requireContext(), "Failed to update book: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi untuk menghapus buku dari Firebase berdasarkan ID
    private fun deleteBookFromFirebase(bookId: String) {
        // Referensi ke node "buku" pada Firebase Database
        val bookDatabase = FirebaseDatabase.getInstance().getReference("buku")

        // Menghapus buku dari Firebase berdasarkan ID
        bookDatabase.child(bookId).removeValue()
            .addOnSuccessListener {
                // Jika penghapusan berhasil, tampilkan pesan sukses dan navigasi ke listBookFragment
                Toast.makeText(requireContext(), "Book deleted successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editBookFragment_to_listBookFragment)
            }
            .addOnFailureListener { error ->
                // Jika penghapusan gagal, tampilkan pesan error
                Toast.makeText(requireContext(), "Failed to delete book: ${error.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi ini dipanggil saat fragment dihancurkan, digunakan untuk membersihkan binding
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Membebaskan referensi binding untuk mencegah memory leak
    }
}
