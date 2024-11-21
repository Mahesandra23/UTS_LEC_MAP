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

    private var isImageSelected = false
    private var isAllTextFilled = false
    private lateinit var btnSaveAddBook: Button
    private lateinit var btnSelectImage: Button
    private lateinit var imageView: ImageView
    private lateinit var placeholderImage: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_add_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase Database and Storage
        database = FirebaseDatabase.getInstance().getReference("buku")
        storage = FirebaseStorage.getInstance().getReference("book_covers")

        // Back button
        view.findViewById<ImageView>(R.id.iv_back_button).setOnClickListener {
            findNavController().popBackStack()
        }

        // Initialize views
        btnSaveAddBook = view.findViewById(R.id.btn_save_add_book)
        btnSelectImage = view.findViewById(R.id.btn_select_image)
        imageView = view.findViewById(R.id.image_view)
        placeholderImage = view.findViewById(R.id.placeholder_image)

        val namaBuku = view.findViewById<EditText>(R.id.nama_buku)
        val penulisBuku = view.findViewById<EditText>(R.id.penulis_buku)
        val sinopsis = view.findViewById<EditText>(R.id.sinopsis)
        val ceritaBuku = view.findViewById<EditText>(R.id.cerita_buku)
        val hargaBuku = view.findViewById<EditText>(R.id.harga_buku)

        // Disable save button initially
        btnSaveAddBook.isEnabled = false
        btnSaveAddBook.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.grey))

        // Set image selection click listener
        btnSelectImage.setOnClickListener {
            selectImageFromGallery()
        }

        // Text change listeners for all EditTexts
        listOf(namaBuku, penulisBuku, sinopsis, ceritaBuku, hargaBuku).forEach { editText ->
            editText.addTextChangedListener {
                isAllTextFilled = listOf(namaBuku, penulisBuku, sinopsis, ceritaBuku, hargaBuku)
                    .all { it.text.isNotEmpty() }
                updateSaveButtonState()
            }
        }

        // Save button action
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
                Toast.makeText(requireContext(), "Please fill all fields and select an image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to enable image selection from gallery
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // Handle image selection result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            if (selectedImageUri != null) {
                imageView.setImageURI(selectedImageUri)
                placeholderImage.visibility = View.GONE // Hide placeholder
                imageView.visibility = View.VISIBLE // Show main image view
                isImageSelected = true
                updateSaveButtonState()
            }
        }
    }

    // Method to upload image to Firebase Storage
    private fun uploadImageToFirebaseStorage(judul: String, penulis: String, harga: Int, sinopsis: String, isi_cerita: String) {
        selectedImageUri?.let { uri ->
            val imageRef = storage.child("${System.currentTimeMillis()}.jpg") // Create a unique file name

            imageRef.putFile(uri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        saveBookToDatabase(judul, penulis, harga, downloadUrl.toString(), sinopsis, isi_cerita)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Method to save book details to the database
    // Method to save book details to the database
    private fun saveBookToDatabase(judul: String, penulis: String, harga: Int, cover: String, sinopsis: String, isi_cerita: String) {
        val bookId = database.push().key ?: return // Generate unique ID
        // Create the Book object with the correct types
        val book = Book(
            id = bookId, // Assuming you want to set the ID as well
            judul = judul,
            penulis = penulis,
            harga = harga, // This is an Int
            cover = cover, // This is a String
            sinopsis = sinopsis,
            isi_cerita = isi_cerita
        )

        // Save to the database
        database.child(bookId).setValue(book)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Book added successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addBookFragment_to_adminFragment) // Navigate back to the admin fragment
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to add book: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // Check validation of both text and image
    private fun updateSaveButtonState() {
        val enableButton = isAllTextFilled && isImageSelected
        btnSaveAddBook.isEnabled = enableButton
        val color = if (enableButton) R.color.black else R.color.grey
        btnSaveAddBook.setBackgroundColor(ContextCompat.getColor(requireContext(), color))
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }
}
