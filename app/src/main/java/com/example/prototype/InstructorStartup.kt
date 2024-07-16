package com.example.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class InstructorStartup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_instructor_startpage)

        val courseMarks: ImageView = findViewById(R.id.imageMarks)
        val newAssessment: ImageView = findViewById(R.id.imageClose)
        val checkResponses: MaterialButton = findViewById(R.id.fab)
        courseMarks.setOnClickListener{
            val intent = Intent(this, CoursesMarks::class.java)
            startActivity(intent)
        }
        newAssessment.setOnClickListener{
            val intent = Intent(this, NewAssessment::class.java)
            startActivity(intent)
        }
        checkResponses.setOnClickListener {
            val intent = Intent(this, AssignedAssessmentsList::class.java)
            startActivity(intent)
        }
    }
}