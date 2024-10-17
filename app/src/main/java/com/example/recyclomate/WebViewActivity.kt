package com.example.recyclomate

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.recyclomate.databinding.ActivityWebViewBinding

class WebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show ProgressBar initially
        binding.progressBar.visibility = View.VISIBLE

        // Set up WebView
        val webView: WebView = binding.webView
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // Show ProgressBar when page starts loading
                binding.progressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // Hide ProgressBar when page finishes loading
                binding.progressBar.visibility = View.GONE
            }
        }

        // Enable JavaScript
        webView.settings.javaScriptEnabled = true

        // Load URL
        val url = intent.getStringExtra(EXTRA_URL) ?: "https://en.wikipedia.org/wiki/Waste_management"
        webView.loadUrl(url)
    }

    companion object {
        const val EXTRA_URL = "extra_url"
    }
}
