package com.example.uts_lec_map
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.uts_lec_map.R

class AddBookFragment : Fragment() {

    private lateinit var selectedImageUri: Uri
    private val PICK_IMAGE_REQUEST = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_book, container, false)

        val imageView = view.findViewById<ImageView>(R.id.image_view)
        val selectImageButton = view.findViewById<Button>(R.id.btn_select_image)
        val namaBukuEditText = view.findViewById<EditText>(R.id.nama_buku)
        val penulisBukuEditText = view.findViewById<EditText>(R.id.penulis_buku)
        val sinopsisEditText = view.findViewById<EditText>(R.id.sinopsis)
        val ceritaBukuEditText = view.findViewById<EditText>(R.id.cerita_buku)
        val addBookButton = view.findViewById<Button>(R.id.btn_add_book)
        val backButton = view.findViewById<ImageView>(R.id.iv_back_button)

        // Handle back button click
        backButton.setOnClickListener {
            findNavController().popBackStack()  // Navigate back
        }

        // Handle image selection
        selectImageButton.setOnClickListener {
            openFileChooser()
        }

        // Handle when the add book button is clicked
        addBookButton.setOnClickListener {
            val namaBuku = namaBukuEditText.text.toString()
            val penulisBuku = penulisBukuEditText.text.toString()
            val sinopsis = sinopsisEditText.text.toString()
            val ceritaBuku = ceritaBukuEditText.text.toString()

            if (namaBuku.isEmpty() || penulisBuku.isEmpty() || sinopsis.isEmpty() || ceritaBuku.isEmpty() || !::selectedImageUri.isInitialized) {
                Toast.makeText(activity, "Please fill out all fields and select an image", Toast.LENGTH_SHORT).show()
            } else {
                // Handle the addition of the book here
                // For example, you might want to send this data to a database or an API
                Toast.makeText(activity, "Book added successfully", Toast.LENGTH_SHORT).show()
                // Clear the fields after successful addition
                namaBukuEditText.text.clear()
                penulisBukuEditText.text.clear()
                sinopsisEditText.text.clear()
                ceritaBukuEditText.text.clear()
                imageView.setImageResource(0) // Clear the image
            }
        }

        return view
    }

    private fun openFileChooser() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            selectedImageUri = data.data!!
            val imageView = view?.findViewById<ImageView>(R.id.image_view)
            imageView?.setImageURI(selectedImageUri) // Set the selected image to ImageView
        }
    }
}
