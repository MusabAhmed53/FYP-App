package com.example.prototype

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AssignedAssessmentsList : AppCompatActivity() {
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArraylist: ArrayList<Assessment>
    private lateinit var assessmentadapter: assessmentResponseAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_studentmarks)
        newArraylist = arrayListOf<Assessment>()

        newRecyclerView = findViewById(R.id.recyclerStudentmarks)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newRecyclerView.addItemDecoration(SimpleItemDecoration(this))
        GlobalScope.launch(Dispatchers.Main) {
            try {
                newArraylist = loadAssessments()
                newRecyclerView.adapter = assessmentResponseAdapter(newArraylist, this@AssignedAssessmentsList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadAssessments(): ArrayList<Assessment> = withContext(Dispatchers.IO) {
        val studentMarksRef = FirebaseDatabase.getInstance().getReference("Assessment")
        suspendCoroutine { continuation ->
            studentMarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val assessmentlist = ArrayList<Assessment>()
                    for (childSnapshot in snapshot.children) {
                        val mark = childSnapshot.getValue(Assessment::class.java)
                        mark?.let {
                            assessmentlist.add(it)
                        }
                    }
                    continuation.resume(assessmentlist)
                }
                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }
}