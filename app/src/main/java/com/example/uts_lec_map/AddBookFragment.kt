package com.example.uts_lec_map

import android.app.Activity
import android.content.Intent
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
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class AddBookFragment : Fragment() {

    private var selectedImageUri: Uri? = null // Changed to nullable
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

        // Set initial button color
        addBookButton.setBackgroundColor(resources.getColor(android.R.color.darker_gray))

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

            // Check for empty fields
            if (namaBuku.isEmpty() || penulisBuku.isEmpty() || sinopsis.isEmpty() || ceritaBuku.isEmpty() || selectedImageUri == null) {
                Toast.makeText(
                    activity,
                    "Please fill out all fields and select an image",
                    Toast.LENGTH_SHORT
                ).show()

                // Set button color to gray if any field is empty
                addBookButton.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
            } else {
                // Handle the addition of the book here
                Toast.makeText(activity, "Book added successfully", Toast.LENGTH_SHORT).show()

                // Clear the fields after successful addition
                namaBukuEditText.text.clear()
                penulisBukuEditText.text.clear()
                sinopsisEditText.text.clear()
                ceritaBukuEditText.text.clear()
                imageView.setImageResource(0) // Clear the image

                // Navigate to AdminFragment after the book is added
                findNavController().navigate(R.id.action_addBookFragment_to_adminFragment)
            }
        }

        // Add a text change listener to reset the button color when fields are filled
        namaBukuEditText.addTextChangedListener { resetButtonColor(addBookButton) }
        penulisBukuEditText.addTextChangedListener { resetButtonColor(addBookButton) }
        sinopsisEditText.addTextChangedListener { resetButtonColor(addBookButton) }
        ceritaBukuEditText.addTextChangedListener { resetButtonColor(addBookButton) }

        // Set up the bottom navigation
        setupBottomNavigation()

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
        if (requestCode == PICK_IMAGE_REQUEST) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.data?.let {
                        selectedImageUri = it
                        val imageView = view?.findViewById<ImageView>(R.id.image_view)
                        imageView?.setImageURI(selectedImageUri) // Set the selected image to ImageView
                    }
                }
                Activity.RESULT_CANCELED -> {
                    Toast.makeText(activity, "Image selection cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Function to reset button color based on fields
    private fun resetButtonColor(addBookButton: Button) {
        val namaBuku = view?.findViewById<EditText>(R.id.nama_buku)?.text.toString()
        val penulisBuku = view?.findViewById<EditText>(R.id.penulis_buku)?.text.toString()
        val sinopsis = view?.findViewById<EditText>(R.id.sinopsis)?.text.toString()
        val ceritaBuku = view?.findViewById<EditText>(R.id.cerita_buku)?.text.toString()

        if (namaBuku.isNotEmpty() && penulisBuku.isNotEmpty() && sinopsis.isNotEmpty() && ceritaBuku.isNotEmpty() && selectedImageUri != null) {
            addBookButton.setBackgroundColor(resources.getColor(R.color.black)) // Set to original color
        } else {
            addBookButton.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.add_book -> {
                    // Already in AddBookFragment, do nothing
                    true
                }
                R.id.book_list -> {
                    // Navigate to ListBookFragment
                    findNavController().navigate(R.id.action_adminFragment_to_bookListFragment)
                    true
                }
                else -> false
            }
        }
    }
}
