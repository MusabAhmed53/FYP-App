package com.example.prototype

import androidx.lifecycle.ViewModelProvider
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

class BarChartFragment : Fragment() {

    companion object {
        fun newInstance() = BarChartFragment()
    }

    private val viewModel: ChartViewModel by activityViewModels()
    private lateinit var barChartView: AnyChartView
    private lateinit var lineChartMarks: ArrayList<StudentMarks>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barChartView = view.findViewById(R.id.barChartView)

        lineChartMarks = ArrayList<StudentMarks>()
        viewModel.studentMarksLiveData.observe(viewLifecycleOwner) { studentMarksList ->
            lineChartMarks = ArrayList<StudentMarks>(studentMarksList.subList(10, minOf(studentMarksList.size, 21)))
            populateAnyChart2(lineChartMarks)
        }
    }

    private fun populateAnyChart2(studentMarks: List<StudentMarks>) {
        val barChart = AnyChart.column()

        val data: MutableList<DataEntry> = ArrayList()
        for ((index, mark) in studentMarks.withIndex()) {
            data.add(ValueDataEntry(mark.studentId, mark.totalMarks))
        }

        barChart.data(data)

        barChart.title("Student Marks Bar Chart")
        barChart.xAxis(0).title("Student ID")
        barChart.yAxis(0).title("Total Marks")
        barChart.animation(true)

        barChart.xAxis(0).stroke("2 #000000")
        barChart.yAxis(0).stroke("2 #000000")
        barChart.xAxis(0).labels().fontWeight("bold")
        barChart.yAxis(0).labels().fontWeight("bold")
        barChart.xAxis(0).title().fontWeight("bold")
        barChart.yAxis(0).title().fontWeight("bold")

        barChartView.setChart(barChart)
    }
}
