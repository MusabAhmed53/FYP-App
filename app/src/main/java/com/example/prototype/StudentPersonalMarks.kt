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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

class StudentPersonalMarks  : AppCompatActivity() {
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var StudentName: String
    private lateinit var newArrayList : ArrayList<StudentMarks>
    private lateinit var objlist : ArrayList<markTablerow>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_student_table)
        newArrayList = arrayListOf()
        objlist = arrayListOf()
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
                    StudentName = name.toString()

                } else {
                    Toast.makeText(this, "No record of this User", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to read User Data", Toast.LENGTH_SHORT).show()
            }
        }

        newRecyclerView = findViewById(R.id.recyclerView)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newRecyclerView.addItemDecoration(SimpleItemDecoration(this))
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val response = readStudentMarks()
                val res = response.firstOrNull()
                res?.assessmentId = "Data Structures"
                val obj = res?.studentId?.let { markTablerow(it, res.totalMarks, res.assessmentId, 100, 45.5) }
                if (obj != null) {
                    objlist.add(obj)
                }
                newRecyclerView.adapter = StudentMarksAdapter(objlist)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun filterMarks(list : ArrayList<StudentMarks>) : ArrayList<StudentMarks>{
        return ArrayList(list.filter { it.studentId == StudentName})
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

