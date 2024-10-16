package com.example.recyclomate.UI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.recyclomate.R
import com.example.recyclomate.databinding.FragmentHomeBinding
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val daysOfWeek = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout using View Binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize the LineChart using binding
        val lineChart: LineChart = binding.LineChart

        // Set data for the LineChart
        setLineChartData(lineChart)

        return binding.root
    }

    private fun setLineChartData(lineChart: LineChart) {
        // Create a list of entries (data points)
        val entries = mutableListOf<Entry>()

        // Example data for pictures clicked each day of the week
        val picturesClicked = listOf(5, 3, 4, 6, 2, 8, 7) // Values for Monday to Sunday

        // Add data points corresponding to each day of the week
        for (i in picturesClicked.indices) {
            entries.add(Entry(i.toFloat(), picturesClicked[i].toFloat())) // Point at (x, y)
        }

        // Create a dataset
        val dataSet = LineDataSet(entries, "Pictures Clicked") // Set a label for the dataset
        dataSet.color = resources.getColor(R.color.teal_200) // Customize color
        dataSet.valueTextColor = resources.getColor(R.color.black) // Customize value text color

        // Create LineData with the dataset
        val lineData = LineData(dataSet)

        // Set the data to the LineChart
        lineChart.data = lineData

        // Customize X-axis labels to show the days of the week
        lineChart.xAxis.valueFormatter = object : ValueFormatter() {
            // This method returns the formatted label for the X-axis
            override fun getFormattedValue(value: Float): String {
                return daysOfWeek.getOrElse(value.toInt()) { "" } // Return the corresponding day
            }
        }

        // Set the number of labels on the X-axis
        lineChart.xAxis.labelCount = daysOfWeek.size

        // Refresh the chart
        lineChart.invalidate() // Refresh the chart
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Clean up the binding reference
    }
}
