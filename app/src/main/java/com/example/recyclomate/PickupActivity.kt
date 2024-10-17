package com.example.recyclomate

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.utils.ObjectUtils
import com.example.recyclomate.databinding.ActivityPickupBinding
import com.example.recyclomate.model.MainViewModel
import com.example.recyclomate.model.PickupData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class PickupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPickupBinding
    private lateinit var image: Bitmap
    private lateinit var viewModel: MainViewModel
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userRef: DatabaseReference = database.getReference(Firebase.auth.currentUser?.uid.toString())

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this , R.layout.activity_pickup)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        bitmap?.let {
//             Load the bitmap into the ImageView
            image = it
            binding.garbageImage.setImageBitmap(it)
        } ?: run {
            Log.e("ImageDisplayActivity", "No Image Bitmap received")
        }

        binding.btnSubmit.setOnClickListener {
            if (Firebase.auth.currentUser == null){
                startActivity(Intent(this , SignInActivity::class.java))
                Toast.makeText(this, "You must SignIn", Toast.LENGTH_SHORT).show()
            }
            else if (binding.etPin.text.length == 6){
                uploadToCloudinaryAndFirebase(image)
                startActivity(Intent(this , MainActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this, "Invalid Pincode", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun uploadToCloudinaryAndFirebase(bitmap: Bitmap) {

        viewModel.increasePickUpCount()
        var pickCount: Int? = null
        userRef.get().addOnSuccessListener { dataSnapshot ->
            pickCount = dataSnapshot.child("pickUp").getValue(Int::class.java) ?: 0

            val publicId = Firebase.auth.currentUser?.uid.toString()+pickCount.toString()
            val pickFolder = "pickUp${pickCount}"

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            val cloudinary = Cloudinary(
                ObjectUtils.asMap(
                    "cloud_name", "diy9goel9",
                    "api_key", "388757982669469",
                    "api_secret", "_5KGeqydWCFWqrfZzCLaG5pxmP0"
                )
            )

            val uploadThread = Thread {
                try {
                    val options = ObjectUtils.asMap(
                        "public_id", publicId
                    )
                    val uploadResult = cloudinary.uploader().upload(data, options)
                    Log.d("Cloudinary", "Upload successful: ${uploadResult["secure_url"]}")
                    // Retrieve the secure URL or any other information from the upload result
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e("Cloudinary", "Upload failed", e)
                }
            }
            val firebaseData = PickupData(
                "https://res.cloudinary.com/diy9goel9/image/upload/${publicId}.jpg",
                binding.etPin.text.toString().toInt()
            )
//            Toast.makeText(this, pickFolder, Toast.LENGTH_SHORT).show()
            val randomKey: String = userRef.push().key.toString()
            userRef.child(randomKey).setValue(firebaseData)

            uploadThread.start()

        }
    }


}