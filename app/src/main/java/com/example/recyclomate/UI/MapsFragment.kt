package com.example.recyclomate.UI

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.recyclomate.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val locationPermissionCode = 1000

    // List of hardcoded dustbin locations
    private val dustbinLocations = listOf(
        LatLng(23.8151, 86.4423),
        LatLng(23.8137, 86.4389),
        LatLng(23.8125, 86.4405),
        LatLng(23.8162, 86.4419),
        LatLng(23.8158, 86.4393),
        LatLng(23.8145, 86.4409),
        // Add more locations as needed
    )

    private val callback = OnMapReadyCallback { map ->
        googleMap = map
        enableUserLocation()

        // Add markers for each dustbin location
        for (location in dustbinLocations) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Dustbin Location")
                    .icon(getResizedBitmapDescriptor(R.drawable.trash, 100, 100))

            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            getUserLocation()
        } else {
            // Request Location Permission
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
    }

    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted; return early or handle accordingly
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLocation = LatLng(it.latitude, it.longitude)
                //googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            enableUserLocation()
        }
    }
    private fun getResizedBitmapDescriptor(resourceId: Int, width: Int, height: Int): BitmapDescriptor {
        // Decode the drawable resource into a Bitmap
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        // Resize the Bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        // Convert the resized Bitmap to a BitmapDescriptor
        return BitmapDescriptorFactory.fromBitmap(resizedBitmap)
    }
}
