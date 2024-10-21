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
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale

open class MainViewModel: ViewModel() {

    private val firestoreRepository: FirestoreRepository = FirestoreRepository()

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val today = LocalDate.now().format(formatter)

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val userRef: DatabaseReference = database.getReference("users").child(Firebase.auth.currentUser?.uid.toString())

    fun increaseStreak(){
        database.getReference("streak").child(Firebase.auth.currentUser?.uid.toString()).get().addOnSuccessListener { dataSnapshot ->
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
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val lastActivity = LocalDate.parse(lastDate, formatter)
        val today = LocalDate.parse(currentDate, formatter)

        return ChronoUnit.DAYS.between(lastActivity, today).toInt()
    }

    fun updateStreakInFirebase(currentDate: String, streakCount: Int) {
        val streakData = mapOf(
            "lastActionDate" to currentDate,
            "streakCount" to streakCount
        )

        database.getReference("streak").child(Firebase.auth.currentUser?.uid.toString()).setValue(streakData).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("Streak updated successfully!")
            } else {
                println("Failed to update streak: ${task.exception}")
            }
        }
    }


    fun increasePickUpCount(){
        database.getReference("pickUp").child(Firebase.auth.currentUser?.uid.toString()).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()){
                var pickUp = dataSnapshot.getValue(Int::class.java) ?: 0

                database.getReference("pickUp").child(Firebase.auth.currentUser?.uid.toString()).setValue(pickUp+1)
            }
            else{
                database.getReference("pickUp").child(Firebase.auth.currentUser?.uid.toString()).setValue(1)
            }
        }
    }

    fun totalRecycle(){
        database.getReference("totalRecycle").child(Firebase.auth.currentUser?.uid.toString()).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()){
                var total = dataSnapshot.getValue(Int::class.java) ?: 0

                database.getReference("totalRecycle").child(Firebase.auth.currentUser?.uid.toString()).setValue(total+1)
            }
            else{

                database.getReference("totalRecycle").child(Firebase.auth.currentUser?.uid.toString()).setValue(1)
            }
        }
    }


}