//package com.example.recyclomate
//
//import android.content.ContentValues.TAG
//import android.content.Intent
//import android.net.Uri
//import android.os.Bundle
//import android.util.Log
//import android.widget.Toast
//import androidx.activity.OnBackPressedCallback
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContentProviderCompat.requireContext
//import androidx.lifecycle.ViewModelProvider
//import com.bumptech.glide.Glide
//import com.cloudinary.Cloudinary
//import com.cloudinary.android.MediaManager
//import com.cloudinary.android.callback.ErrorInfo
//import com.cloudinary.android.callback.UploadCallback
//import com.cloudinary.utils.ObjectUtils
//import com.example.recyclomate.MainActivity
//import com.example.recyclomate.R
//import com.example.recyclomate.databinding.ActivitySignInBinding
//import com.example.recyclomate.model.MainViewModel
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.GoogleAuthProvider
//
//class SignInActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivitySignInBinding
//    private lateinit var firebaseAuth: FirebaseAuth
//    private lateinit var googleSignInClient: GoogleSignInClient
//
//    override fun onStart() {
//        super.onStart()
//        val currentUser: FirebaseUser? = firebaseAuth.currentUser
//        if (currentUser != null) {
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }
//    }
////
////    object CloudinaryConfig {
////        val cloudinary: Cloudinary = Cloudinary(
////            ObjectUtils.asMap(
////            "cloud_name", "your_cloud_name",
////            "api_key", "your_api_key",
////            "api_secret", "your_api_secret"
////        ))
////    }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySignInBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        firebaseAuth = FirebaseAuth.getInstance()
//
////        MediaManager.init(this@SignInActivity , mapOf(
////            "cloud_name" to "diy9goel9",
////            "api_key" to "388757982669469",
////            "secure" to true
////        )
//
//        binding.gotosignup.setOnClickListener {
//            val intent = Intent(this, SignUpActivity::class.java)
//            startActivity(intent)
//        }
//        // Google Sign-In options
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        googleSignInClient = GoogleSignIn.getClient(this, gso)
//
//        // Google sign-in button
//        binding.googleSignInButton.setOnClickListener {
//            val signInIntent = googleSignInClient.signInIntent
//            startActivityForResult(signInIntent, RC_SIGN_IN)
//        }
//
//        binding.buttonSignin.setOnClickListener {
//            val email = binding.emailEt.text.toString()
//            val pass = binding.passET.text.toString()
//
//            if (email.isNotEmpty() && pass.isNotEmpty()) {
//                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                    } else {
//                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        // Guest access button
//        binding.guest.setOnClickListener {
//            startActivity(Intent(this, MainActivity::class.java))
//        }
//
//
//        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                finish()
//            }
//        })
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        Log.d(TAG, "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode")
//
//        if (requestCode == RC_SIGN_IN) {
//            Log.d(TAG, "Google Sign-In request code matched")
//
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            if (task.isSuccessful) {
//                Log.d(TAG, "Google Sign-In task successful")
//
//                val account = task.result
//                Log.d(TAG, "Google Account obtained: ${account?.email}")
//
//                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
//
//                // Log the profile photo URL if available
//                val profilePhotoUrl = account?.photoUrl?.toString()
//                Log.d(TAG, "Profile Photo URL: $profilePhotoUrl")
//
//                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
//                    if (signInTask.isSuccessful) {
//                        Log.d(TAG, "Firebase sign-in successful")
//
//                        startActivity(Intent(this, MainActivity::class.java))
//                        finish()
//                    } else {
//                        Log.e(TAG, "Firebase sign-in failed: ${signInTask.exception?.message}")
//                        Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
//                    }
//                }
//            } else {
//                Log.e(TAG, "Google Sign-In task failed: ${task.exception?.message}")
//                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
//            }
//        } else {
//            Log.d(TAG, "Request code did not match RC_SIGN_IN")
//        }
//    }
//
//    // Function to upload the profile photo to Cloudinary
//    private fun uploadProfilePhotoToCloudinary(photoUrl: String) {
//        // Assuming you have initialized your Cloudinary instance
//        val uri = Uri.parse(photoUrl)
//        MediaManager.get().upload(uri)
//            .unsigned("profilePic") // Replace with your unsigned preset name
//            .option("public_id", firebaseAuth.currentUser?.uid) // Use user ID as public_id
//            .callback(object : UploadCallback {
//                override fun onStart(requestId: String) {
//                    Toast.makeText(this@SignInActivity, "Upload Started", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
//                    // Update progress if needed
//                }
//
//                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
//                    val url = resultData["secure_url"] as String
//                    // Here you can save the URL to your database or use it as needed
//                    Log.d("UploadSuccess", "Profile photo uploaded successfully: $url")
//                    Toast.makeText(this@SignInActivity, "Upload Successful", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onError(requestId: String, error: ErrorInfo) {
//                    Toast.makeText(this@SignInActivity, "Upload Failed: ${error.description}", Toast.LENGTH_SHORT).show()
//                }
//
//                override fun onReschedule(requestId: String, error: ErrorInfo) {
//                    // Reschedule if needed
//                }
//            })
//            .dispatch()
//    }
//
//    companion object {
//        private const val RC_SIGN_IN = 9001
//    }
//}
package com.example.recyclomate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.example.recyclomate.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var username: String? = null // Variable for username

    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = firebaseAuth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.gotosignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Google sign-in button
        binding.googleSignInButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.buttonSignin.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Show the ProgressBar and disable the button
                binding.progressBar.visibility = View.VISIBLE
                binding.buttonSignin.isEnabled = false

                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    // Hide the ProgressBar and enable the button after the operation
                    binding.progressBar.visibility = View.GONE
                    binding.buttonSignin.isEnabled = true

                    if (task.isSuccessful) {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }
        }

        // Guest access button
        binding.guest.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        // Back button handling
        // binding.backButton.setOnClickListener {
        //     onBackPressedDispatcher.onBackPressed()
        // }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                // Get the profile photo URL from the Google account
                val profilePhotoUrl = account.photoUrl.toString()

                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        // Start MainActivity after successful sign-in
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to upload the profile photo to Cloudinary
    private fun uploadProfilePhotoToCloudinary(photoUrl: String) {
        // Assuming you have initialized your Cloudinary instance
        val uri = Uri.parse(photoUrl)
        MediaManager.get().upload(uri)
            .unsigned("profilePic") // Replace with your unsigned preset name
            .option("public_id", firebaseAuth.currentUser?.uid) // Use user ID as public_id
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Toast.makeText(this@SignInActivity, "Upload Started", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Update progress if needed
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as String
                    // Here you can save the URL to your database or use it as needed
                    Log.d("UploadSuccess", "Profile photo uploaded successfully: $url")
                    Toast.makeText(this@SignInActivity, "Upload Successful", Toast.LENGTH_SHORT).show()
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Toast.makeText(this@SignInActivity, "Upload Failed: ${error.description}", Toast.LENGTH_SHORT).show()
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    // Reschedule if needed
                }
            })
            .dispatch()
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
