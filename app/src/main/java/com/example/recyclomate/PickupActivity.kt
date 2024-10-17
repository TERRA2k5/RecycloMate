package com.example.recyclomate

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
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
    private val userRef: DatabaseReference =
        database.getReference(Firebase.auth.currentUser?.uid.toString())
    val randomKey: String = userRef.push().key.toString()


    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pickup)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

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
            val pincode = binding.etPin.text.toString()
            var validator = 0
            for (i in pincode) {
                if (i.isDigit()) validator++
            }
            if (Firebase.auth.currentUser == null) {
                startActivity(Intent(this, SignInActivity::class.java))
                Toast.makeText(this, "You must SignIn", Toast.LENGTH_SHORT).show()
            } else if (binding.etPin.text.length == 6 && validator == 6) {
                uploadToCloudinaryAndFirebase(image)
                getUserLocation()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid Pincode", Toast.LENGTH_SHORT).show()
            }
        }

    }

    fun uploadToCloudinaryAndFirebase(bitmap: Bitmap) {

        viewModel.increasePickUpCount()
        var pickCount: Int? = null
        userRef.get().addOnSuccessListener { dataSnapshot ->
            pickCount = dataSnapshot.child("pickUp").getValue(Int::class.java) ?: 0

            val publicId = randomKey

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
            uploadThread.start()

        }
    }

    private fun getUserLocation(): LatLng? {
        var userLocation: LatLng? = null
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted; return early or handle accordingly
            Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show()
            return null
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
//                Toast.makeText(context, it.longitude.toString(), Toast.LENGTH_SHORT).show()
                userLocation = LatLng(it.latitude, it.longitude)
                val firebaseData = PickupData(
                    randomKey.toString(),
                    "https://res.cloudinary.com/diy9goel9/image/upload/${randomKey}.jpg",
                    binding.etPin.text.toString().toInt(),
                    it.latitude,
                    it.longitude
                )
                userRef.child(randomKey).setValue(firebaseData)

                //googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
            }
        }

        return userLocation
//        Toast.makeText(context, userLocation?.latitude.toString() , Toast.LENGTH_SHORT).show()
    }


}