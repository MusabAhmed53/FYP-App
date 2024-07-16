package com.example.prototype

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import java.util.ArrayList

class HistogramFragment : Fragment() {
    companion object {
        fun newInstance() = HistogramFragment()
    }

    private val viewModel: ChartViewModel by activityViewModels()
    private lateinit var HistogramView: AnyChartView
    private lateinit var HistogramMarks: ArrayList<StudentMarks>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_histogram, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HistogramView = view.findViewById(R.id.histogramView)

        HistogramMarks = ArrayList<StudentMarks>()
        viewModel.studentMarksLiveData.observe(viewLifecycleOwner) { studentMarksList ->
            HistogramMarks = ArrayList<StudentMarks>(studentMarksList.subList(10, minOf(studentMarksList.size, 21)))
            populateAnyChart1(HistogramMarks)
        }
    }

    private fun populateAnyChart1(studentMarks: ArrayList<StudentMarks>) {
        val barChart = AnyChart.bar()
        val markFrequencies = HashMap<Int, Int>()

        for (mark in studentMarks) {
            if (markFrequencies.containsKey(mark.totalMarks)) {
                markFrequencies[mark.totalMarks] = markFrequencies[mark.totalMarks]!! + 1
            } else {
                markFrequencies[mark.totalMarks] = 1
            }
        }

        val data: MutableList<DataEntry> = ArrayList()
        for ((totalMarks, frequency) in markFrequencies) {
            data.add(ValueDataEntry(totalMarks, frequency))
        }

        barChart.data(data)
        barChart.title("Student Marks Histogram")
        barChart.xAxis(0).title("Total Marks")
        barChart.yAxis(0).title("Frequency")

        barChart.animation(true)
        barChart.xAxis(0).stroke("2 #000000")
        barChart.yAxis(0).stroke("2 #000000")
        barChart.xAxis(0).labels().fontWeight("bold")
        barChart.yAxis(0).labels().fontWeight("bold")
        barChart.xAxis(0).title().fontWeight("bold")
        barChart.yAxis(0).title().fontWeight("bold")

        HistogramView.setChart(barChart)
    }


}