package com.example.recyclomate

import android.animation.Animator
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recyclomate.databinding.ActivityImageDisplayBinding
import com.example.recyclomate.model.MainViewModel

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

        val bitmap = intent.getParcelableExtra<Bitmap>("imageBitmap")
        bitmap?.let {
            // Load the bitmap into the ImageView
            binding.imageView.setImageBitmap(it)
            binding.imageView.visibility = View.VISIBLE // Make sure to set visibility to visible
        } ?: run {
            Log.e("ImageDisplayActivity", "No Image Bitmap received")
        }

        // Hide the progress bar after loading the image
        binding.progressBar.visibility = View.GONE

        // Set up the button click listener to play animation and navigate
        binding.uploadButton.setOnClickListener {
            playAnimationAndNavigate()
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
        binding.organic.visibility= View.GONE
        binding.inorganic.visibility= View.GONE
        binding.benifit.visibility= View.GONE
        binding.editText1.visibility= View.GONE
        binding.editText2.visibility= View.GONE
        binding.editText3.visibility= View.GONE

        // Add a listener to detect when the animation finishes
        binding.lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                // After the animation ends, navigate to MainActivity (or HomeFragment)
                val intent = Intent(this@ImageDisplayActivity, MainActivity::class.java)
                startActivity(intent)
                finish() // Optional: finish the current activity
            }
        binding.uploadButton.setOnClickListener {
            viewModel.increaseStreak()
            viewModel.totalRecycle()
        }

        // Hide the progress bar after loading the image
        binding.progressBar.visibility = View.GONE
            override fun onAnimationCancel(animation: Animator) {}

            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun loadImage() {
        // Show the progress bar while loading the image
        binding.progressBar.visibility = View.VISIBLE

        // Load the image
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
