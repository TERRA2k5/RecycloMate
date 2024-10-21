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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cameraIntent: Int = 0

    private val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var picturesClicked: MutableList<Int> = mutableListOf(2, 4, 1, 6, 10, 5, 8)

    // Gson instance with lenient parsing
//    private val gson by lazy {
//        GsonBuilder()
//            .setLenient()
//            .create()
//    }
//
//    private val retrofit by lazy {
//        Retrofit.Builder()
//            .baseUrl("http://192.168.42.249:5000/")  // Update to your correct API URL
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//    }


    // API Service instance
//    private val apiService by lazy {
//        retrofit.create(ApiService::class.java)
//    }

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.42.249:5000/") // Replace with your base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService = retrofit.create(ApiService::class.java)

    // Activity result launcher for camera
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                val bitmap = data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    handleCameraResult(bitmap)
                } else {
                    Toast.makeText(requireContext(), "Failed to capture image.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val lineChart: LineChart = binding.LineChart
        lineChart.setTouchEnabled(false)
        lineChart.isDragEnabled = false
        lineChart.setScaleEnabled(false)
        lineChart.setDrawGridBackground(false)

        setLineChartData(lineChart)

        binding.camera.setOnClickListener{
            var openGallary: Intent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(openGallary, 1)

        }
        binding.pickup.setOnClickListener {
//            cameraIntent = 1
            openCamera()
        }

        binding.guidevideo.setOnClickListener {

            val intent = Intent(requireContext(), GuideActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
    var imageUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data?.data
                CoroutineScope(Dispatchers.IO).launch {
                    uploadImage(requireContext(),imageUri!!)
                }
            }
        }

    }

    suspend fun uploadImage(context: Context, bitmap: Uri) {

        val gson = GsonBuilder()
            .setLenient()
            .create()

        val client = OkHttpClient.Builder().build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.42.249:5000/")  // Replace with your base URL
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val apiService: ApiService = retrofit.create(ApiService::class.java)

        val filePath = getRealPathFromUri(imageUri!!) // Implement this function to get file path
        val file = File(filePath!!)


        // Check if file is created successfully
        if (file != null) {
            // Prepare the image part for form-data
            val imagePart = prepareImagePart(file, "file")  // 'file' is the key that the server expects

            // Since this is a suspend function, no need for Call or enqueue
            try {
                val response = apiService.uploadImage(imagePart)  // Make the network call

                // Handle the response
                val displayImageIntent = Intent(context, ImageDisplayActivity::class.java).apply {
                    putExtra("imageBitmap", bitmap)
                    putExtra("output", response.toString())
                }
                context.startActivity(displayImageIntent)
            } catch (e: Exception) {
                println("Upload failed: ${e.message}")
            }
        } else {
            println("Failed to create file from Bitmap")
        }
    }


    private fun getRealPathFromUri(contentUri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        activity?.contentResolver?.query(contentUri, projection, null, null, null)?.use { cursor ->
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }
        return null
    }

    fun prepareImagePart(file: File, partName: String): MultipartBody.Part {
        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }


//    fun prepareImagePart(filePath: String, partName: String): MultipartBody.Part {
//        val file = File(filePath)
//
//        // Create RequestBody for the file
//        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//
//        // Create MultipartBody.Part using the file, file name and part name
//        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
//    }

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher.launch(cameraIntent)
        } else {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun handleCameraResult(bitmap: Bitmap) {
//        when (cameraIntent) {
//            0 -> {
////                sendImage(bitmap)  // Send the image to the server
//                CoroutineScope(Dispatchers.IO).launch {
//                    uploadImage(requireContext(),bitmap)
//                }
////                val displayImageIntent = Intent(requireContext(), ImageDisplayActivity::class.java)
////                displayImageIntent.putExtra("imageBitmap", bitmap)
////                startActivity(displayImageIntent)
//            }
//            1 -> {
                val pickupIntent = Intent(requireContext(), PickupActivity::class.java)
                pickupIntent.putExtra("imageBitmap", bitmap)
                startActivity(pickupIntent)
//            }
//        }
    }
//
//    fun bitmapToFile(context: Context, bitmap: Bitmap): File? {
//        val file = File(context.cacheDir, "image_${System.currentTimeMillis()}.png") // Create a file in the cache directory
//        file.outputStream().use { out ->
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) // Compress}
//        }
//            return  file
//    }
//
//    // Prepare image part for multipart request
//    fun prepareImagePart(file: File, partName: String): MultipartBody.Part {
//        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
//        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
//    }
//
//    suspend fun uploadImage(context: Context, bitmap: Bitmap) {
//
//        val gson = GsonBuilder()
//            .setLenient()
//            .create()
//
//        val client = OkHttpClient.Builder().build()
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl("http://192.168.42.249:5000/")  // Replace with your base URL
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build()
//
//        val apiService: ApiService = retrofit.create(ApiService::class.java)
//
//        // Convert Bitmap to File
//        val imageFile = bitmapToFile(context, bitmap)
////        Toast.makeText(requireContext(), imageFile.toString() , Toast.LENGTH_SHORT).show()
//        // Check if file is created successfully
//        if (imageFile != null) {
//            // Prepare the image part for form-data
//            val imagePart = prepareImagePart(imageFile, "file")
//
//            // Since this is a suspend function, no need for Call or enqueue
//            try {
//                val response = apiService.uploadImage(imagePart)  // Make the network call
//
//                // Handle the response
//                val displayImageIntent = Intent(context, ImageDisplayActivity::class.java)
//                    displayImageIntent.putExtra("imageBitmap", bitmap)
//                Toast.makeText(requireContext(), response.toString() , Toast.LENGTH_SHORT).show()
//                    displayImageIntent.putExtra("output", response.toString())
//
//                startActivity(displayImageIntent)
//            } catch (e: Exception) {
//                println("oad failed: ${e.message}")
//            }
//        } else {
//            println("Failed to create file from Bitmap")
//        }
//    }



//    private fun sendImage(bitmap: Bitmap) {
//        // Convert bitmap to byte array
//        val byteArray = bitmapToByteArray(bitmap)
//        // Prepare image part for the multipart request
//        val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)
//        val filePart = MultipartBody.Part.createFormData("file", "image.jpg", requestFile)
//
//        // Use coroutine to make the API call
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // Make the API call
//                val response = apiService.uploadImage(filePart)
//
//                // Switch to Main thread for UI updates
//                withContext(Dispatchers.Main) {
//                    if (response.isSuccessful) {
//                        // Display success message
//                        val result = response.body()
//                        Toast.makeText(requireContext(), result?.message ?: "Image uploaded successfully!", Toast.LENGTH_LONG).show()
//                    } else {
//                        // Display error message
//                        Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_LONG).show()
//                    }
//                }
//            } catch (e: Exception) {
//                // Handle any exceptions (network failures, etc.)
//                withContext(Dispatchers.Main) {
//                    Toast.makeText(requireContext(), "Failed to upload image: ${e.message}", Toast.LENGTH_LONG).show()
//                }
//            }
//        }
//    }
//
//
//    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//        return stream.toByteArray()
//    }

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
