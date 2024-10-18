package com.example.recyclomate

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

        binding.uploadButton.setOnClickListener {
            viewModel.increaseStreak()
            viewModel.totalRecycle()
        }

        // Hide the progress bar after loading the image
        binding.progressBar.visibility = View.GONE
    }


}
