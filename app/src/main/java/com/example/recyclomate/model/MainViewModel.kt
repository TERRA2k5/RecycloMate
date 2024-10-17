package com.example.recyclomate.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

open class MainViewModel: ViewModel() {

    var isMediaManagerInit: Boolean = false
//    var picState: Any? = false

    private val firestoreRepository: FirestoreRepository = FirestoreRepository()
    val mydate = Date(System.currentTimeMillis() - (1000 * 60 * 60 * 24))
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
//    val yestr = dateFormat.format(mydate)

    val mydate1 = Date(System.currentTimeMillis())
    val today = dateFormat.format(mydate1)

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val userRef: DatabaseReference = database.getReference("users").child(Firebase.auth.currentUser?.uid.toString())

    fun increaseStreak(){
        userRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val lastActionDate = dataSnapshot.child("lastActionDate").getValue(String::class.java)
                val streakCount = dataSnapshot.child("streakCount").getValue(Int::class.java) ?: 0

                if (lastActionDate != null) {
                    val dateDifference = calculateDateDifference(lastActionDate, today)

                    when {
                        dateDifference == 1 -> {
                            val updatedStreakCount = streakCount + 1
                            updateStreakInFirebase(today, updatedStreakCount)
                        }
                        dateDifference > 1 -> {
                            updateStreakInFirebase(today, 1)
                        }
                        else -> {
                            Log.i("tagy" ,"Action already performed today.")
                        }
                    }
                } else {
                    updateStreakInFirebase(today, 1)
                }
            }
            else {
                updateStreakInFirebase(today, 1)
            }
        }
    }

    fun calculateDateDifference(lastDate: String, currentDate: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val last = sdf.parse(lastDate)
        val current = sdf.parse(currentDate)

        // Calculate difference in days
        val diffInMillis = current.time - last.time
        return (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
    }

    fun updateStreakInFirebase(currentDate: String, streakCount: Int) {
        val streakData = mapOf(
            "lastActionDate" to currentDate,
            "streakCount" to streakCount
        )

        userRef.setValue(streakData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Streak updated successfully!")
            } else {
                println("Failed to update streak: ${task.exception}")
            }
        }
    }


    fun getStreak(): Int{
        var streakCount = 0
        userRef.child("streakCount").get().addOnSuccessListener { dataSnapshot ->
            streakCount = dataSnapshot.getValue(Int::class.java) ?: 0
        }
        return streakCount
    }


}