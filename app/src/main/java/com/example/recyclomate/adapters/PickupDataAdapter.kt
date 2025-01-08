package com.example.recyclomate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recyclomate.R
import com.example.recyclomate.model.PickupData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class PickupDataAdapter(
    private val context: Context,
    private var pickupDataList: MutableList<PickupData>
) : RecyclerView.Adapter<PickupDataAdapter.MyViewHolder>() {

    private lateinit var auth: FirebaseAuth
    private val userRef: DatabaseReference =
        Firebase.database.getReference(Firebase.auth.currentUser?.uid.toString())


    // ViewHolder class for holding the views
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView
        val textViewPincode: TextView

        init {
            imageView = itemView.findViewById(R.id.imageView)
            textViewPincode = itemView.findViewById(R.id.textViewPincode)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.history_card, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pickupDataList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentPickup = pickupDataList[position]

        holder.textViewPincode.text = currentPickup.pincode.toString()
        Glide.with(context).load(currentPickup.image.toString()).into(holder.imageView)

    }

    // Method to update the list when new data arrives
    fun updateList(newList: List<PickupData>) {
        pickupDataList.clear()
        pickupDataList.addAll(newList)
        notifyDataSetChanged()
    }
}
