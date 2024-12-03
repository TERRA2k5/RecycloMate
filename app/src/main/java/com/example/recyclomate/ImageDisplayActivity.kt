package com.example.recyclomate

import android.animation.Animator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recyclomate.databinding.ActivityImageDisplayBinding
import com.example.recyclomate.model.MainViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ImageDisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDisplayBinding
    private lateinit var imageUri: Uri
    lateinit var viewModel: MainViewModel

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the image bitmap from the intent
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Retrieve the Bitmap from the intent
//        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        // Retrieve the URI from the intent (assuming it's passed)
        imageUri = intent.getParcelableExtra<Uri>("imageUri") ?: Uri.EMPTY

        // Load the image into the ImageView
//        bitmap?.let {
//            binding.imageView.setImageBitmap(it)
//            binding.imageView.visibility = View.VISIBLE
//        } ?: run {
//            Log.e("ImageDisplayActivity", "No Image Bitmap received")
//        }

        // Hide the progress bar after loading the image
        binding.progressBar.visibility = View.GONE

        binding.uploadButton.setOnClickListener {
            if(Firebase.auth.currentUser != null ){
                viewModel.totalRecycle()
                viewModel.increaseStreak()
                viewModel.update7dayCount()
                viewModel.CleanUp()
            }

            playAnimationAndNavigate()
        }


        // Load the image if URI is available
        if (imageUri != Uri.EMPTY) {
            loadImage()
        }
    }

    private fun playAnimationAndNavigate() {
        // Show the Lottie animation, make it cover the entire screen
        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.lottieAnimationView.playAnimation()

        // Hide other views so the animation covers the whole screen
        binding.imageView.visibility = View.GONE
        binding.uploadButton.visibility = View.GONE
        binding.textviewhead.visibility = View.GONE
        binding.organic.visibility = View.GONE
        binding.inorganic.visibility = View.GONE
        binding.benifit.visibility = View.GONE
        binding.editText1.visibility = View.GONE
        binding.editText2.visibility = View.GONE
        binding.editText3.visibility = View.GONE


        // Add a listener to detect when the animation finishes
        binding.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // After the animation ends, navigate to MainActivity
                val intent = Intent(this@ImageDisplayActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: finish the current activity
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun loadImage() {
        // Show the progress bar while loading the image
        binding.progressBar.visibility = View.VISIBLE

        // Load the image using URI
        try {
            binding.imageView.setImageURI(imageUri)
            binding.imageView.visibility = View.VISIBLE // Make sure to set visibility to visible
        } catch (e: Exception) {
            Log.e("ImageDisplayActivity", "Error loading image: ${e.message}")
        } finally {
            // Hide the progress bar after loading the image
            binding.progressBar.visibility = View.GONE
        }
    }
}
