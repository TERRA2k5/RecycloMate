package com.example.recyclomate.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid ?: throw IllegalStateException("No user logged in")

    suspend fun updatePicState(state: Any){
        try {
            db.collection("Profile").document(userId).set(state).await()
        }catch (e: Exception){
            Log.i("Tsgy" , "Connectivity Fail")
        }
    }
    suspend fun getPicState(): Any?{
        try {
            val state = db.collection("Profile").document(userId).get().await()
            return state
        }catch (e: Exception){
            Log.i("Tsgy" , "Connectivity Fail")
            return null
        }
    }
}