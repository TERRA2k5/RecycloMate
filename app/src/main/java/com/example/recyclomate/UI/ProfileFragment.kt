package com.example.recyclomate.UI

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.recyclomate.MainActivity
import com.example.recyclomate.R
import com.example.recyclomate.SignInActivity
import com.example.recyclomate.databinding.FragmentProfileBinding
import com.example.recyclomate.model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var selectedImageUri: Uri
    private lateinit var firebaseAuth: FirebaseAuth
    private var username: String? = null
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userRef: DatabaseReference = database.getReference("users").child(Firebase.auth.currentUser?.uid.toString())

//    private var isMediaManagerInit = false


    override fun onStart() {
        super.onStart()

        userRef.get().addOnSuccessListener { dataSnapshot->
            val pickCount = dataSnapshot.child("pickUp").getValue(Int::class.java) ?: 0
            binding.tvNumberPickup.text = pickCount.toString()
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        if (Firebase.auth.currentUser != null){
            binding.btnLog.text ="Logout"
            binding.tvName.text = Firebase.auth.currentUser?.displayName.toString()

            if(Firebase.auth.currentUser?.photoUrl != null ) Glide.with(this).load(Firebase.auth.currentUser?.photoUrl).into(binding.profileIMG)
        }
        else{
            binding.tvName.text = "Guest"
            binding.profileIMG.isClickable = false
            binding.contriCard.visibility = View.GONE
            binding.tvStreak.text = "SignIn and start tracking you Recycles !!"
        }

        binding.btnLog.setOnClickListener {
            if(Firebase.auth.currentUser != null){
                firebaseAuth.signOut()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                binding.btnLog.text = "SignIn"
                // Redirect to SignInActivity
                activity?.finishAffinity()
            }
            else{
                startActivity(Intent(context , SignInActivity::class.java))
            }
        }
        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser?.uid

        // Fetch the username from the logged-in user
        username = user ?: "default_username" // Provide a default username if displayName is null



        binding.profileIMG.setOnClickListener {
            openImagePicker()

        }
        return binding.root
    }

    private fun openImagePicker() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to change Profile Picture?")

        builder.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        builder.setNegativeButton("No") { dialog, which -> dialog.cancel() }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data!!

            val profileUpdate = userProfileChangeRequest {
                photoUri = selectedImageUri
            }

            Firebase.auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                Glide.with(this).load(Firebase.auth.currentUser?.photoUrl).into(binding.profileIMG)
            }
        }
    }

//    private fun fetchImage(username: String, imageView: ImageView) {
//        // Construct the URL for the image using the username as public_id
//        val imageUrl = "https://res.cloudinary.com/diy9goel9/image/upload/profile/$username.jpg"
//
//        // Load the image into the provided ImageView using Glide
//        try{
//            Glide.with(this)
//                .load(imageUrl)
//                .into(imageView)
//        }catch (e: Exception){
//            binding.profileIMG.setImageResource(R.drawable.default_profile)
//        }
}
