package com.example.recyclomate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclomate.R
import com.example.recyclomate.databinding.ActivityGuideBinding

class GuideActivity : AppCompatActivity() {

    // View Binding object
    private lateinit var binding: ActivityGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set text for waste management overview
        binding.wastemangmentoverview.text = getString(R.string.waste_management_overview)

        // Set click listener for YouTube thumbnail
        binding.youtubeThumbnail5.setOnClickListener {
            val videoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
            startActivity(videoIntent)
        }

        // Set click listener for "Know More" text using binding
        binding.knowMore3.setOnClickListener {
            // Create an Intent to start WebViewActivity
            val intent = Intent(this, WebViewActivity::class.java)
            startActivity(intent)
        }

    }
}



