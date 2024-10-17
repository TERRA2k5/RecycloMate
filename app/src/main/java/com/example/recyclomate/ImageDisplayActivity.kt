package com.example.recyclomate

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclomate.databinding.ActivityImageDisplayBinding

class ImageDisplayActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDisplayBinding
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDisplayBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the image bitmap from the intent
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
