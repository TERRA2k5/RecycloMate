package com.example.recyclomate


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.recyclomate.databinding.ActivitySignUpBinding
import com.example.recyclomate.model.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import java.util.regex.Matcher
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.buttonSignup.setOnClickListener {

            val username = binding.userEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passEt.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (emailValidator(email)) {
                    if (pass == confirmPass) {
                        // Show the ProgressBar
                        binding.progressBar.visibility = View.VISIBLE

                        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                            // Hide the ProgressBar
                            binding.progressBar.visibility = View.GONE

                            if (task.isSuccessful) {
                                val profileUpdate = userProfileChangeRequest {
                                    displayName = username
                                }

                                task.result.user!!.updateProfile(profileUpdate)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Log.d("username", "User profile updated.")
                                            val i = Intent(this, MainActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                            startActivity(i)
                                            finish()
                                        }
                                    }
                            } else {
                                Toast.makeText(this, "Account Creation failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()

                            }
                        }


                    } else {
                        Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Email is Invalid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
            }

        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun emailValidator(email: String): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN: String =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }
}
