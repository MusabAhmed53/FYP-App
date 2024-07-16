package com.example.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.random.Random

class CoursesMarks : AppCompatActivity() {
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArraylist: ArrayList<Course>
    private lateinit var courseadapter: CoursesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.coursesmarks)
        newArraylist = arrayListOf<Course>()

        newRecyclerView = findViewById(R.id.recyclerCourses)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newRecyclerView.addItemDecoration(SimpleItemDecoration(this))
        val ass1 = Course("History 101", "Bilal", "3456")
        val ass2 = Course("English Lit", "Bilal", "3456")
        val ass3 = Course("Geography Year1", "Bilal", "3456")
        newArraylist.add(ass1)
        newArraylist.add(ass2)
        newArraylist.add(ass3)
        newRecyclerView.adapter = CoursesAdapter(newArraylist)
    }

    private fun getUserData()
    {
        if(newArraylist.isNotEmpty()){
            newArraylist.clear()
        }
        val ass1 = Course("History 101", "Bilal", "3456")
        val ass2 = Course("English Lit", "Bilal", "3456")
        val ass3 = Course("Geography Year1", "Bilal", "3456")
        newArraylist.add(ass1)
        newArraylist.add(ass2)
        newArraylist.add(ass3)

        courseadapter = CoursesAdapter(newArraylist)
        newRecyclerView.adapter = CoursesAdapter(newArraylist)
    }

    private fun generateRandomStudentMark(): StudentMarks {
        val assessmentId = "Colonialism Quiz"
        val courseId = "History 101"
        val totalMarks = Random.nextInt(0, 101)
        val studentId = "Student${Random.nextInt(1, 501)}"
        return StudentMarks(assessmentId, courseId, totalMarks, studentId)
    }

    private fun main() {
        val database = Firebase.database
        val studentMarksRef = database.getReference("StudentMarks")

        repeat(500) {
            val studentMark = generateRandomStudentMark()
            studentMarksRef.push().setValue(studentMark)
                .addOnSuccessListener {
                    println("Student mark added successfully: $studentMark")
                }
                .addOnFailureListener { e ->
                    println("Error adding student mark: $e")
                }
        }
    }
}