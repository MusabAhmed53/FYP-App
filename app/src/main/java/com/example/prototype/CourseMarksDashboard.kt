package com.example.prototype

import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.prototype.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.util.ArrayList;
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class CourseMarksDashboard : AppCompatActivity() {

    private lateinit var studentMarks: ArrayList<StudentMarks>
    private lateinit var database: DatabaseReference
    private val chartViewModel: ChartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coursemarksdashboard)

        val btnLineChart: Button = findViewById(R.id.btnLineChart)
        val btnBarChart: Button = findViewById(R.id.btnBarChart)
        val btnHistogram: Button = findViewById(R.id.histogramView)
        val btnStatTable: Button=findViewById(R.id.btntableview)
        val fragmentContainer: FrameLayout = findViewById(R.id.fragmentContainer)
        val barChartFragment = BarChartFragment()
        val lineChartFragment = LineChartFragment()
        val histogramFragment = HistogramFragment()
        val statTableFragment= StatTableFragment()



        btnStatTable.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, statTableFragment)
                .commit()
        }

        btnBarChart.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, barChartFragment)
                .commit()
        }

        btnLineChart.setOnClickListener{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, lineChartFragment)
                .commit()
        }

        btnHistogram.setOnClickListener{
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, histogramFragment)
                .commit()
        }

        studentMarks = ArrayList()
        database = FirebaseDatabase.getInstance().getReference("StudentMarks")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                studentMarks = readStudentMarks()
                chartViewModel.studentMarksLiveData.value = studentMarks
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun readStudentMarks(): ArrayList<StudentMarks> = withContext(Dispatchers.IO) {
        val studentMarksRef = FirebaseDatabase.getInstance().getReference("StudentMarks")
        suspendCoroutine { continuation ->
            studentMarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val studentMarks = ArrayList<StudentMarks>()
                    for (childSnapshot in snapshot.children) {
                        val mark = childSnapshot.getValue(StudentMarks::class.java)
                        mark?.let {
                            studentMarks.add(it)
                        }
                    }
                    continuation.resume(studentMarks)
                }
                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
}