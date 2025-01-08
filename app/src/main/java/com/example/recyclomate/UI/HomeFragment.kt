package com.example.recyclomate.UI

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.recyclomate.GuideActivity
import com.example.recyclomate.ImageDisplayActivity
import com.example.recyclomate.PickupActivity
import com.example.recyclomate.R
import com.example.recyclomate.databinding.FragmentHomeBinding
import com.example.recyclomate.instance.ApiService
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody

import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cameraIntent: Int = 0
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val uid = Firebase.auth.uid.toString()
    private val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var picturesClicked: MutableList<Int> = mutableListOf(2, 4, 1, 6, 10, 5, 8)




    var imageUri: Uri? = null
    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()){
        imageUri = it!!
        uploadImage()
    }


    // Activity result launcher for camera
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the camera launcher
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val data = result.data
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    if (bitmap != null) {
                        handleCameraResult(bitmap)
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Failed to capture image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        UpdateUI()

        val lineChart: LineChart = binding.LineChart
        lineChart.setTouchEnabled(false)
        lineChart.isDragEnabled = false
        lineChart.setScaleEnabled(false)
        lineChart.setDrawGridBackground(false)

        setLineChartData(lineChart)

        binding.uploadML.setOnClickListener {
            contract.launch("image/*")
        }
        binding.pickup.setOnClickListener {
            cameraIntent = 1
            openCamera()
        }

        binding.guidevideo.setOnClickListener {

            val intent = Intent(requireContext(), GuideActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }


    fun uploadImage() {
        val fileDir = activity?.applicationContext?.filesDir
        val file = File(fileDir, "image.png")
        val inputstream = activity?.contentResolver?.openInputStream(imageUri!!)
        val outputstream = FileOutputStream(file)
        inputstream!!.copyTo(outputstream)

        val requestbody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestbody)

        val retrofit = Retrofit.Builder().baseUrl("http://172.22.113.118:5000")
            .addConverterFactory(GsonConverterFactory.create()).build()
            .create(ApiService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            val response = retrofit.retrofitUpload(part)
            if(response.body() != null){
                val result = response.body()!!.string()
                val intent = Intent(requireContext(), ImageDisplayActivity::class.java)
                intent.putExtra("imageUri", imageUri)
                intent.putExtra("result", result)
                Log.i("TAGY", result)
                startActivity(intent)
            }
        }
//        Toast.makeText(context, "Error uploading image", Toast.LENGTH_SHORT).show()
    }


    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        } else {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        }
    }

    private fun handleCameraResult(bitmap: Bitmap) {

                val pickupIntent = Intent(requireContext(), PickupActivity::class.java)
                pickupIntent.putExtra("imageBitmap", bitmap)
                startActivity(pickupIntent)

    }

    private fun setLineChartData(lineChart: LineChart) {
        val entries = mutableListOf<Entry>()
        for (i in picturesClicked.indices) {
            entries.add(Entry(i.toFloat(), picturesClicked[i].toFloat()))
        }
        val dataSet = LineDataSet(entries, "Pictures Clicked")
        dataSet.color = resources.getColor(R.color.teal_200, null)
        dataSet.valueTextColor = resources.getColor(R.color.black, null)
        val lineData = LineData(dataSet)
        lineChart.data = lineData
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return daysOfWeek.getOrElse(value.toInt()) { "" }
            }
        }
        lineChart.xAxis.labelCount = daysOfWeek.size
        lineChart.axisLeft.axisMinimum = 0f
        lineChart.axisRight.isEnabled = false
        lineChart.invalidate()
    }

    fun UpdateUI() {

        database.getReference("streak").child(uid).child("streakCount").get().addOnSuccessListener {
            val streak: Int
            if (it.exists()) {
                streak = it.getValue(Int::class.java) ?: 0
            } else {
                streak = 0
            }

            var string = "${streak} Days in a Row!!"
            if (streak == 0) {
                string = "Start Recycling Now!!"
            }

            binding.tvStreakCnt.text = string
        }
    }

    fun updateDataForNewDay(newData: Int) {
        picturesClicked.removeAt(0)
        picturesClicked.add(newData)
        setLineChartData(binding.LineChart)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }
}
