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

class LineChartFragment : Fragment() {
    companion object {
        fun newInstance() = LineChartFragment()
    }

    private val viewModel: ChartViewModel by activityViewModels()
    private lateinit var LineChartView: AnyChartView
    private lateinit var lineChartMarks: ArrayList<StudentMarks>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_line_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LineChartView = view.findViewById(R.id.linechartView)

        lineChartMarks = ArrayList<StudentMarks>()
        viewModel.studentMarksLiveData.observe(viewLifecycleOwner) { studentMarksList ->
            lineChartMarks = ArrayList<StudentMarks>(studentMarksList.subList(10, minOf(studentMarksList.size, 21)))
            populateAnyChart1(lineChartMarks)
        }
    }

    private fun populateAnyChart1(studentMarks: ArrayList<StudentMarks>) {
        val lineChart = AnyChart.line()

        val data: MutableList<DataEntry> = ArrayList()
        for ((index, mark) in studentMarks.withIndex()) {
            data.add(ValueDataEntry(mark.studentId, mark.totalMarks))
        }

        lineChart.data(data)
        lineChart.title("Student Marks Line Chart")
        lineChart.xAxis(0).title("Student ID")
        lineChart.yAxis(0).title("Total Marks")
        lineChart.animation(true)


        lineChart.xAxis(0).stroke("2 #000000")
        lineChart.yAxis(0).stroke("2 #000000")
        lineChart.xAxis(0).labels().fontWeight("bold")
        lineChart.yAxis(0).labels().fontWeight("bold")
        lineChart.xAxis(0).title().fontWeight("bold")
        lineChart.yAxis(0).title().fontWeight("bold")

        LineChartView.setChart(lineChart)
    }


}