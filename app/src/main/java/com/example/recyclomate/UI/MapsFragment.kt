package com.example.recyclomate.UI

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.recyclomate.R
import com.example.recyclomate.databinding.FragmentMapsBinding
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
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

class MapsFragment : Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var googleMap: GoogleMap
    private val locationPermissionCode = 1000
    lateinit var binding: FragmentMapsBinding

    // List of hardcoded dustbin locations
    private val dustbinLocations = listOf(
        LatLng(23.8131, 86.4452),
        LatLng(23.8151, 86.4423),
        LatLng(23.8137, 86.4389),
        LatLng(23.8125, 86.4405),
        LatLng(23.8162, 86.4419),
        LatLng(23.8158, 86.4393),
        LatLng(23.8145, 86.4409),
        LatLng(23.8124, 86.4446),
        LatLng(23.8127, 86.4418),
        LatLng(23.8142, 86.4435),
        LatLng(23.8116, 86.4390),
        LatLng(23.8110, 86.4408),
        // Add more locations as needed
    )

    private val recycleCenterLocation = listOf(
        LatLng(23.8479 , 86.4777),
        LatLng(23.7368 , 86.4164),
        LatLng(23.8054 , 86.1979),
        LatLng(24.2596 , 85.8758),
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

        for (location in recycleCenterLocation) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title("Recycling Center")
                    .icon(getResizedBitmapDescriptor(R.drawable.recycle_symbol, 100, 100))

            )
        }
    }

    fun CalcDistance( latlog1: LatLng ,latlog2: LatLng ): Double{


        val x = abs(latlog1.latitude - latlog2.latitude)
        val y = abs(latlog1.longitude - latlog2.longitude)

        val distance = x.pow(2) + y.pow(2)

        return distance
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_maps, container, false)

        binding.trashBtn.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions are not granted; return early or handle accordingly
                Toast.makeText(context, "Location Access Denied", Toast.LENGTH_SHORT).show()
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
//                    Toast.makeText(context, it.longitude.toString(), Toast.LENGTH_SHORT).show()
                    val userLocation = LatLng(it.latitude, it.longitude)

                    var closest_ind = 0
                    var closest_dist = CalcDistance(userLocation , dustbinLocations[0])

                    for(i in dustbinLocations.indices){
                        val binLocation = dustbinLocations[i]
                        val distance = CalcDistance(binLocation , userLocation)

                        if(distance < closest_dist){
                            closest_dist = distance
                            closest_ind = i
                        }
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dustbinLocations[closest_ind], 18f))
                }
            }
        }

        binding.recycleBtn.setOnClickListener{
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permissions are not granted; return early or handle accordingly
                Toast.makeText(context, "Location Access Denied", Toast.LENGTH_SHORT).show()
            }
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
//                    Toast.makeText(context, it.longitude.toString(), Toast.LENGTH_SHORT).show()
                    val userLocation = LatLng(it.latitude, it.longitude)

                    var closest_ind = 0
                    var closest_dist = CalcDistance(userLocation , recycleCenterLocation[0])

                    for(i in recycleCenterLocation.indices){
                        val binLocation = recycleCenterLocation[i]
                        val distance = CalcDistance(binLocation , userLocation)

                        if(distance < closest_dist){
                            closest_dist = distance
                            closest_ind = i
                        }
                    }

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(recycleCenterLocation[closest_ind], 18f))
                }
            }
        }

        return binding.root
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

    private fun getUserLocation(){
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
//                Toast.makeText(context, it.longitude.toString(), Toast.LENGTH_SHORT).show()
                val userLocation = LatLng(it.latitude, it.longitude)
                //googleMap.addMarker(MarkerOptions().position(userLocation).title("You are here"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f))
            }
        }
//        Toast.makeText(context, userLocation?.latitude.toString() , Toast.LENGTH_SHORT).show()
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
