package com.example.recyclomate.model

import com.google.android.gms.maps.model.LatLng

data class PickupData(
    val id: String = "sd",
    val image: String,
    val pincode: Int,
    val latitude: Double = 99.0,
    val longitute: Double = 00.8
)
