package com.example.recyclomate.UI

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.recyclomate.R
import com.example.recyclomate.SignInActivity
import com.example.recyclomate.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import okhttp3.internal.wait

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var selectedImageUri: Uri
    private lateinit var firebaseAuth: FirebaseAuth
    private var username: String? = null
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val uid = Firebase.auth.currentUser?.uid.toString()


    override fun onStart() {
        super.onStart()

//        binding.swipeRefreshLayout.isRefreshing = true
        firebaseAuth = Firebase.auth
        binding.btnLog.setOnClickListener {
            if (Firebase.auth.currentUser != null) {
                firebaseAuth.signOut()
                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT)
                    .show()
                binding.btnLog.text = "SignIn"
                // Redirect to SignInActivity
                activity?.finishAffinity()
            } else {
                startActivity(Intent(context, SignInActivity::class.java))
            }
        }

        binding.profileIMG.setOnClickListener {
            openImagePicker()

        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            onStart()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        UpdateUI()

//        Toast.makeText(context, refreshCloser.toString(), Toast.LENGTH_SHORT).show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)


        if (Firebase.auth.currentUser != null) {
            binding.btnLog.text = "Logout"
            binding.tvName.text = Firebase.auth.currentUser?.displayName.toString()

            if (Firebase.auth.currentUser?.photoUrl != null) Glide.with(this)
                .load(Firebase.auth.currentUser?.photoUrl).into(binding.profileIMG)
        } else {
            binding.tvName.text = "Guest"
            binding.profileIMG.isClickable = false
            binding.contriCard.visibility = View.GONE
            binding.tvStreak.text = "SignIn and start tracking you Recycles !!"
        }

//
//        // Initialize FirebaseAuth
//        firebaseAuth = FirebaseAuth.getInstance()
//        val user = firebaseAuth.currentUser?.uid
//
//        // Fetch the username from the logged-in user
//        username = user ?: "default_username" // Provide a default username if displayName is null
////
////        val pickUplist = mutableListOf<PickupData>()
////        userRef.get().addOnSuccessListener { dataSnapshot ->
////            if (dataSnapshot.exists()) {
////                for (i in dataSnapshot.children) {
////                    userRef.child(i.toString()).get().addOnSuccessListener { it->
////                        val data = i.getValue(PickupData::class.java)
////                        pickUplist.add(data!!)
////                    }
////                }
////            }
////
////            val adapter = PickupDataAdapter(requireContext() , pickUplist)
////            binding.pickupRecyclerView?.adapter = adapter
////
////            binding.pickupRecyclerView?.layoutManager = GridLayoutManager(context, 2)
////        }
        return binding.root

    }

    private fun openImagePicker() {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Do you want to change Profile Picture?")

        builder.setPositiveButton("Yes") { dialog, which ->
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        builder.setNegativeButton("No") { dialog, which -> dialog.cancel() }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data!!

            val profileUpdate = userProfileChangeRequest {
                photoUri = selectedImageUri
            }

            Firebase.auth.currentUser?.updateProfile(profileUpdate)?.addOnCompleteListener {
                Glide.with(this).load(Firebase.auth.currentUser?.photoUrl).into(binding.profileIMG)
            }
        }
    }

    fun UpdateUI() {

        database.getReference("streak").child(uid).child("streakCount").get().addOnSuccessListener {
            var streak = 0
            if (it.exists()) {
                streak = it.getValue(Int::class.java) ?: 0
            } else {
                streak = 0
            }

            val string = "Congratulations on your ${streak} Days Recycling Streak!"

            binding.tvStreak.text = string
        }

        database.getReference("pickUp").child(uid).get().addOnSuccessListener {
            if (it.exists()) {
                binding.tvNumberPickup.text = (it.getValue(Int::class.java) ?: 0).toString()
            } else binding.tvNumberPickup.text = "0"
        }

        database.getReference("totalRecycle").child(uid).get().addOnSuccessListener {
            if (it.exists()) {
                binding.tvGarbageRecycled.text =
                    (it.getValue(Int::class.java) ?: 0).toString()
            } else binding.tvGarbageRecycled.text = "0"
        }
    }

}
