package com.example.prototype

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.prototype.R
import java.util.ArrayList
import kotlin.math.sqrt


class StatTableFragment : Fragment() {

    companion object {
        fun newInstance() =StatTableFragment()
    }

    private val viewModel: ChartViewModel by activityViewModels()
    private lateinit var MinScoreView: TextView
    private lateinit var MaxScoreView: TextView
    private lateinit var MeanScoreView: TextView
    private lateinit var Std_DevView: TextView

    private lateinit var TableMarks: ArrayList<StudentMarks>
    override fun onCreateView(


        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stat_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MinScoreView = view.findViewById(R.id.MinScore)
        MaxScoreView = view.findViewById(R.id.MaxScore)
        MeanScoreView = view.findViewById(R.id.MeanScore)
        Std_DevView = view.findViewById(R.id.Std_Dev)

        viewModel.studentMarksLiveData.observe(viewLifecycleOwner) { studentMarksList ->
            TableMarks = ArrayList<StudentMarks>(studentMarksList.subList(10, minOf(studentMarksList.size, 21)))

            if (TableMarks.isNotEmpty()) {
                val mean = TableMarks.map { it.totalMarks }.average()
                val formattedMean = String.format("%.2f", mean)
                val stdDev = stan_dev(TableMarks)
                val formattedStdDev = String.format("%.2f", stdDev)

                MeanScoreView.text = formattedMean
                Std_DevView.text = formattedStdDev
                MinScoreView.text = TableMarks.minOf { it.totalMarks }.toString()
                MaxScoreView.text = TableMarks.maxOf { it.totalMarks }.toString()
            } else {
                MeanScoreView.text = "N/A"
                Std_DevView.text = "N/A"
                MinScoreView.text = "N/A"
                MaxScoreView.text = "N/A"
            }
        }
    }

    fun stan_dev(studentmarks: ArrayList<StudentMarks>): Double {
        // Step 1: Calculate the mean
        val mean = studentmarks.map { it.totalMarks }.average()

        // Step 2: Calculate the variance
        val variance = studentmarks.map { it.totalMarks }
            .map { (it - mean) * (it - mean) }
            .average()

        // Step 3: Calculate the standard deviation
        val standardDeviation = sqrt(variance)

        return standardDeviation
    }
}