package com.example.recyclomate.UI

import android.Manifest
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.recyclomate.GuideActivity
import com.example.recyclomate.ImageDisplayActivity
import com.example.recyclomate.PickupActivity
import com.example.recyclomate.R
import com.example.recyclomate.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var cameraIntent: Int = 0

    private val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private var picturesClicked: MutableList<Int> = mutableListOf(2, 4, 1, 6, 10, 5, 8)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using View Binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val lineChart: LineChart = binding.LineChart

//        Toast.makeText(context, Firebase.auth.currentUser?.displayName , Toast.LENGTH_SHORT).show()

        lineChart.setTouchEnabled(false)
        lineChart.isDragEnabled = false
        lineChart.setScaleEnabled(false)
        lineChart.setDrawGridBackground(false)


        setLineChartData(lineChart)

        binding.camera.setOnClickListener {
            cameraIntent = 0
            openCamera()
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

    private fun openCamera() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if (cameraIntent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
            }
        } else {

            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                openCamera()
            } else {

                Toast.makeText(requireContext(), "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            data?.let {

                val bitmap = it.extras?.get("data") as? Bitmap
                if (bitmap != null) {

                    if (cameraIntent == 0){
                        val displayImageIntent = Intent(requireContext(), ImageDisplayActivity::class.java)
                        displayImageIntent.putExtra("imageBitmap", bitmap)
                        startActivity(displayImageIntent)
                        activity?.finish()
                    }
                    else{
                        val displayImageIntent = Intent(requireContext(), PickupActivity::class.java)
                        displayImageIntent.putExtra("imageBitmap", bitmap)
                        startActivity(displayImageIntent)
                        activity?.finish()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to capture image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setLineChartData(lineChart: LineChart) {
        val entries = mutableListOf<Entry>()
        for (i in picturesClicked.indices) {
            entries.add(Entry(i.toFloat(), picturesClicked[i].toFloat()))
        }
        val dataSet = LineDataSet(entries, "Pictures Clicked")
        dataSet.color = resources.getColor(R.color.teal_200)
        dataSet.valueTextColor = resources.getColor(R.color.black)
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
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_CAMERA_PERMISSION = 1001
    }
}



