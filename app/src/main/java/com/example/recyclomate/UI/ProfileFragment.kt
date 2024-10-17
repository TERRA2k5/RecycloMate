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
import com.google.firebase.ktx.Firebase

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var selectedImageUri: Uri
    private lateinit var firebaseAuth: FirebaseAuth
    private var username: String? = null // Variable for username

//    private var isMediaManagerInit = false

//    val cloudinary = Cloudinary(
//        mapOf(
//            "cloud_name" to "YOUR_CLOUD_NAME",
//            "api_key" to "YOUR_API_KEY",
//            "secure" to true
//        )
//    )

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

//        if(!viewModel.isMediaManagerInit){
//            MediaManager.init(requireContext(), mapOf(
//                "cloud_name" to "diy9goel9",
//                "api_key" to "388757982669469",
//                "secure" to true
//            ))
//            viewModel.isMediaManagerInit = true
//        }
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



//        if(firebaseAuth.currentUser==null){
//            val imageUrl = "https://res.cloudinary.com/diy9goel9/image/upload/default_profile.jpg"
//
//            // Load the image into the provided ImageView using Glide
//            Glide.with(this)
//                .load(imageUrl)
//                .into(binding.profileIMG)
//        }else{
//            try {
//                fetchImage(username!!, binding.profileIMG) // Use non-null asserted call since username has a default value
//            } catch (e: Exception) {
//                Log.e("ProfileError", "No pic found", e)
//            }
//        }

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
//            uploadImage(selectedImageUri)
            val profileUpdate = userProfileChangeRequest {
                photoUri = selectedImageUri
            }
//            val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//            viewModel.updatePicState(true)

            Firebase.auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                Glide.with(this).load(Firebase.auth.currentUser?.photoUrl).into(binding.profileIMG)
            }
        }
    }

//    private fun uploadImage(uri: Uri) {
//
//        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
//
//        val filePath = getRealPathFromURI(uri)
//        if (filePath != null) {
//            MediaManager.get().upload(uri)
//                .unsigned("profilePic")
//                .option("public_id", username)
//                .callback(object : UploadCallback {
//                    override fun onStart(requestId: String) {
//                        // Upload started
//                        Toast.makeText(context, "Upload Started", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
//                        // Update progress if needed
//                    }
//
//                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
//                        val url = resultData["secure_url"] as String
//                        Glide.with(requireContext())
//                            .load(url)
//                            .into(binding.profileIMG)
//
//                        viewModel.updatePicState(true)
//
//                        Toast.makeText(context, "Upload Successful", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onError(requestId: String, error: ErrorInfo) {
//                        Toast.makeText(context, "Upload Failed: ${error.description}", Toast.LENGTH_SHORT).show()
//                    }
//
//                    override fun onReschedule(requestId: String, error: ErrorInfo) {
//                        // Reschedule if needed
//                    }
//                })
//                .dispatch()
//        } else {
//            Toast.makeText(context, "Unable to get file path", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    private fun getRealPathFromURI(uri: Uri): String? {
//        var path: String? = null
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = activity?.contentResolver?.query(uri, projection, null, null, null)
//        if (cursor != null) {
//            if (cursor.moveToFirst()) {
//                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//                path = cursor.getString(columnIndex)
//            }
//            cursor.close()
//        }
//        return path
//    }
//
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
//    }
}
