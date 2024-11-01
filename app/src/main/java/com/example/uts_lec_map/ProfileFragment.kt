package com.example.uts_lec_map

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.uts_lec_map.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var isEditing = false
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var userId: String
    private lateinit var storage: FirebaseStorage
    private var imageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            binding.profileImage.setImageURI(imageUri)
            uploadImageToFirebase(imageUri)
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo: Bitmap = result.data?.extras?.get("data") as Bitmap
            binding.profileImage.setImageBitmap(photo)
            uploadImageToFirebase(photo)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("users")
        storage = FirebaseStorage.getInstance()
        userId = auth.currentUser?.uid.toString()

        setupBottomNavigation()
        loadUserProfile()

        binding.profileImage.setOnClickListener { showImageOptions() }
        setupEditProfileButton()

        return binding.root
    }

    private fun showImageOptions() {
        val options = arrayOf("Choose from Gallery", "Capture with Camera")
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Choose Profile Image")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> selectImageFromGallery()
                1 -> openCamera()
            }
        }
        builder.show()
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(intent)
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 102)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 102 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(context, "Camera permission is required to capture profile photo", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.profile

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
                    true
                }
                R.id.search -> {
                    findNavController().navigate(R.id.action_profileFragment_to_searchFragment)
                    true
                }
                R.id.history -> {
                    findNavController().navigate(R.id.action_profileFragment_to_historyFragment)
                    true
                }
                R.id.profile -> true
                else -> false
            }
        }
    }

    private fun loadUserProfile() {
        database.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val email = snapshot.child("email").value.toString()
                    val phone = snapshot.child("phone").value.toString()
                    val profileImageUrl = snapshot.child("profileImageUrl").value.toString()

                    binding.usernameEditText.setText(name)
                    binding.emailEditText.setText(email)
                    binding.phoneEditText.setText(phone)

                    if (profileImageUrl.isNotEmpty()) {
                        Glide.with(this@ProfileFragment)
                            .load(profileImageUrl)
                            .into(binding.profileImage)
                    }
                } else {
                    Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupEditProfileButton() {
        binding.editProfileButton.setOnClickListener {
            isEditing = !isEditing

            if (isEditing) {
                enableEditing(true)
                binding.saveButton.visibility = View.VISIBLE
                binding.editProfileButton.text = "Cancel"
            } else {
                enableEditing(false)
                binding.saveButton.visibility = View.GONE
                binding.editProfileButton.text = "Edit Profile"
            }
        }

        binding.saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    private fun enableEditing(enable: Boolean) {
        binding.usernameEditText.isEnabled = enable
        binding.emailEditText.isEnabled = enable
        binding.phoneEditText.isEnabled = enable
    }

    private fun saveUserProfile() {
        val name = binding.usernameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()

        val userUpdates = mapOf(
            "name" to name,
            "email" to email,
            "phone" to phone,
        )

        database.child(userId).updateChildren(userUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                isEditing = false
                enableEditing(false)
                binding.saveButton.visibility = View.GONE
                binding.editProfileButton.text = "Edit Profile"
            } else {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase(uri: Uri?) {
        uri?.let {
            val storageRef: StorageReference = storage.reference.child("profile_images/$userId.jpg")
            storageRef.putFile(it).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    database.child(userId).child("profileImageUrl").setValue(downloadUri.toString())
                    Toast.makeText(context, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase(bitmap: Bitmap) {
        val storageRef: StorageReference = storage.reference.child("profile_images/$userId.jpg")
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        baos.close()

        storageRef.putBytes(data).addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                database.child(userId).child("profileImageUrl").setValue(downloadUri.toString())
                Toast.makeText(context, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}