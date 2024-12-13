package com.example.uts_lec_map

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.uts_lec_map.models.Book
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddBookFragment : Fragment() {

    // Variabel untuk melacak apakah gambar sudah dipilih
    private var isImageSelected = false

    // Variabel untuk melacak apakah semua input teks sudah diisi
    private var isAllTextFilled = false

    // Deklarasi variabel tombol dan tampilan
    private lateinit var btnSaveAddBook: Button
    private lateinit var btnSelectImage: Button
    private lateinit var imageView: ImageView
    private lateinit var placeholderImage: ImageView

    // Variabel untuk menyimpan URI gambar yang dipilih
    private var selectedImageUri: Uri? = null

    // Referensi ke Firebase Database dan Storage
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Menghubungkan fragment dengan layout
        return inflater.inflate(R.layout.fragment_add_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inisialisasi Firebase Database dan Storage
        database = FirebaseDatabase.getInstance().getReference("buku")
        storage = FirebaseStorage.getInstance().getReference("book_covers")

        // Tombol untuk kembali ke halaman sebelumnya
        view.findViewById<ImageView>(R.id.iv_back_button).setOnClickListener {
            findNavController().popBackStack()
        }

        // Inisialisasi elemen UI
        btnSaveAddBook = view.findViewById(R.id.btn_save_add_book)
        btnSelectImage = view.findViewById(R.id.btn_select_image)
        imageView = view.findViewById(R.id.image_view)
        placeholderImage = view.findViewById(R.id.placeholder_image)

        // EditText untuk input data buku
        val namaBuku = view.findViewById<EditText>(R.id.nama_buku)
        val penulisBuku = view.findViewById<EditText>(R.id.penulis_buku)
        val sinopsis = view.findViewById<EditText>(R.id.sinopsis)
        val ceritaBuku = view.findViewById<EditText>(R.id.cerita_buku)
        val hargaBuku = view.findViewById<EditText>(R.id.harga_buku)

        // Awalnya tombol save dinonaktifkan
        btnSaveAddBook.isEnabled = false
        btnSaveAddBook.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))

        // Menambahkan listener untuk memilih gambar
        btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        // Menambahkan listener perubahan teks pada semua EditText
        listOf(namaBuku, penulisBuku, sinopsis, ceritaBuku, hargaBuku).forEach { editText ->
            editText.addTextChangedListener {
                isAllTextFilled = listOf(namaBuku, penulisBuku, sinopsis, ceritaBuku, hargaBuku)
                    .all { it.text.isNotEmpty() } // Cek apakah semua input terisi
                updateSaveButtonState()
            }
        }

        // Aksi tombol save
        btnSaveAddBook.setOnClickListener {
            if (isImageSelected && isAllTextFilled) {
                uploadImageToFirebaseStorage(
                    namaBuku.text.toString(),
                    penulisBuku.text.toString(),
                    hargaBuku.text.toString().toInt(),
                    sinopsis.text.toString(),
                    ceritaBuku.text.toString()
                )
            } else {
                Toast.makeText(requireContext(), "Harap isi semua kolom dan pilih gambar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Metode untuk memilih gambar dari galeri
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // Menangani hasil dari pemilihan gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri)
                placeholderImage.visibility = View.GONE // Sembunyikan placeholder
                imageView.visibility = View.VISIBLE // Tampilkan gambar utama
                isImageSelected = true
                updateSaveButtonState()
            }
        }
    }

    // Metode untuk mengunggah gambar ke Firebase Storage
    private fun uploadImageToFirebaseStorage(judul: String, penulis: String, harga: Int, sinopsis: String, isi_cerita: String) {
        selectedImageUri?.let { uri ->
            val imageRef = storage.child("${System.currentTimeMillis()}.jpg") // Nama file unik
            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveBookToDatabase(judul, penulis, harga, downloadUrl.toString(), sinopsis, isi_cerita)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal mengunggah gambar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Metode untuk menyimpan data buku ke Firebase Database
    private fun saveBookToDatabase(judul: String, penulis: String, harga: Int, cover: String, sinopsis: String, isi_cerita: String) {
        val bookId = database.push().key ?: return // ID unik untuk buku
        val book = Book(
            id = bookId,
            judul = judul,
            penulis = penulis,
            harga = harga,
            cover = cover,
            sinopsis = sinopsis,
            isi_cerita = isi_cerita
        )

        // Simpan data ke database
        database.child(bookId).setValue(book)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Buku berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addBookFragment_to_adminFragment) // Navigasi ke halaman admin
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Gagal menambahkan buku: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Metode untuk memperbarui status tombol save
    private fun updateSaveButtonState() {
        val enableButton = isAllTextFilled && isImageSelected
        btnSaveAddBook.isEnabled = enableButton
        val color = if (enableButton) R.color.black else R.color.grey
        btnSaveAddBook.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1 // Kode permintaan untuk memilih gambar
    }
}
