package com.example.prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class NewAssessment : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var assessment_name : EditText
    private lateinit var total_marks : EditText
    private lateinit var question : EditText
    private lateinit var instructorname: String
    private lateinit var course_list: ArrayList<Course>
    private lateinit var question_add_btn: Button
    private lateinit var questions_list: ArrayList<String>
    private lateinit var question_marks_list: ArrayList<String>
    private lateinit var question_marks: EditText
    private lateinit var uploadBtn: Button
    private lateinit var dropdown : AutoCompleteTextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_assessment)
        assessment_name = findViewById(R.id.etGroupFour)
        total_marks = findViewById(R.id.etGroupTwo)
        question = findViewById(R.id.edxQuestions)
        question_add_btn = findViewById(R.id.button)
        questions_list = ArrayList()
        question_marks_list = ArrayList()
        question_marks = findViewById(R.id.edxQuestionsmarks)
        uploadBtn = findViewById(R.id.btnUpload)
        question_add_btn.setOnClickListener{
            addQuestion()
        }
        firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = firebaseAuth.currentUser
        val uid: String? = user?.uid
        database = FirebaseDatabase.getInstance().getReference("User")
        if (uid != null) {
            database.child(uid).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    var email = dataSnapshot.child("email").value as String?
                    var name = dataSnapshot.child("name").value as String?
                    var password = dataSnapshot.child("password").value as String?
                    var userType = dataSnapshot.child("userType").value as String?
                    instructorname = name.toString()

                } else {
                    Toast.makeText(this, "No record of this User", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to read User Data", Toast.LENGTH_SHORT).show()
            }
        }
        dropdown = findViewById(R.id.autocCompleteTextView2)
        var itemList : ArrayList<String> = ArrayList()
        course_list = ArrayList()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                if (uid != null){
                    course_list = loadCourses(uid)
                    for (course in course_list)
                    {
                        itemList.add(course.name.toString())
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        for (course in course_list)
        {
            itemList.add(course.name.toString())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_selectable_list_item, itemList)
        dropdown.setAdapter(adapter)

        uploadBtn.setOnClickListener{
            addAssessment()
        }

    }

    suspend fun loadCourses(uid:String?): ArrayList<Course> = withContext(Dispatchers.IO) {
        val studentMarksRef = FirebaseDatabase.getInstance().getReference("Courses$uid")
        suspendCoroutine { continuation ->
            studentMarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val course_list = ArrayList<Course>()
                    for (childSnapshot in snapshot.children) {
                        val mark = childSnapshot.getValue(Course::class.java)
                        mark?.let {
                            course_list.add(it)
                        }
                    }
                    continuation.resume(course_list)
                }
                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    private fun addQuestion(){
        val ques = question.text
        val mark = question_marks.text
        questions_list.add(ques.toString())
        question_marks_list.add(mark.toString())
        question.text.clear()
        question_marks.text.clear()
    }

    private fun addAssessment()
    {
        val name = assessment_name.text.toString()
        val totalmarks = total_marks.text.toString()
        val course = dropdown.text.toString()
        val A1 = Assessment(name, totalmarks, course, instructorname, false, questions_list, question_marks_list)
        database = FirebaseDatabase.getInstance().getReference("Assessment")
        if (name != "")
        {
            database.child(name).setValue(A1).addOnCompleteListener { dbTask ->
                if (dbTask.isSuccessful) {
                    Toast.makeText(this, "Assessment Added Successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to add Assessment", Toast.LENGTH_SHORT).show()
                    Log.e("DatabaseError", dbTask.exception?.message ?: "Unknown error")
                }
            }
        }
        else {
            Toast.makeText(this, "didn't add assessment name", Toast.LENGTH_SHORT).show()
        }
    }
}

