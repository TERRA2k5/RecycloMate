package com.example.recyclomate.model

import com.google.android.gms.maps.model.LatLng

data class PickupData(
    val id: String,
    val image: String,
    val pincode: Int,
    val latitude: Double,
    val longitute: Double
)
