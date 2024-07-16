package com.example.prototype

import ServerResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers.Default
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.serialization.decodeFromString

class StudentResponses : AppCompatActivity() {
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArraylist: ArrayList<Response>
    private lateinit var api_list: ArrayList<APIobject>
    private lateinit var studentNames: Array<String>
    private lateinit var gradedresponses: Array<ParsedResponse>
    private lateinit var btnCheckplaig : Button
    private lateinit var btnGraderesponses : Button
    private var assessment_name: String = "N/A"
    private lateinit var checked_assessment: Assessment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentresponses)
        assessment_name = intent.getStringExtra("assessmentId") ?: "N/A"
        newArraylist = arrayListOf()
        api_list = arrayListOf()
        studentNames = arrayOf()
        gradedresponses = arrayOf()
        btnCheckplaig = findViewById(R.id.Check_plag_btn)
        btnGraderesponses = findViewById(R.id.Grade_response_btn)
        newRecyclerView = findViewById(R.id.recyclerStudentresponses)
        newRecyclerView.layoutManager = LinearLayoutManager(this)
        newRecyclerView.setHasFixedSize(true)
        newRecyclerView.addItemDecoration(SimpleItemDecoration(this))
        searchElementByName(assessment_name) { assessment ->
            if (assessment != null) {
                checked_assessment = assessment
            }
        }
        loadDummyResponses()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val responses = loadResponses()
                newArraylist = filterResponsesByAssessmentName(responses, assessment_name)
                newRecyclerView.adapter = ResponsesAdapter(newArraylist, this@StudentResponses)
                loadAPIobjects()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        btnCheckplaig.setOnClickListener{
            CoroutineScope(Default).launch {
                PlaigCheck()
            }
        }
        btnGraderesponses.setOnClickListener {
            CoroutineScope(Default).launch {
                gradeResponses()
            }
        }

    }

    fun filterResponsesByAssessmentName(responseList: ArrayList<Response>, assessmentName: String): ArrayList<Response> {
        return ArrayList(responseList.filter { it.assessment_name.toString() == assessmentName })
    }

    fun loadDummyResponses()
    {
        val Dbref = FirebaseDatabase.getInstance().getReference("Responses")
        val obj = Response("Grace", 0, assessment_name,
            Answers = arrayListOf(
                "To simulate the behavior of portions of the desired software product.",
                "The testing stage can influence both the coding stage (phase 5) and the solution refinement stage (phase 7).",
                "At the main function."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
            )
        val obj1 = Response("Hammond", 0, assessment_name,
            Answers = arrayListOf(
                "To provide a proof of concept and validate initial design ideas.",
                "It impacts the design phase by identifying flaws that require redesign.",
                "C++ programs start execution at the main() function."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
        )
        val obj2 = Response("Alice", 0, assessment_name,
            Answers = arrayListOf(
                "To identify and address potential issues early in the development process.",
                "The testing stage influences the requirements phase by revealing misunderstood or missing requirements.",
                "The entry point for C++ programs is the main function."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
        )
        val obj3 = Response("John", 0, assessment_name,
            Answers = arrayListOf(
                "To gather user feedback and refine requirements based on real interactions.",
                "It affects the implementation stage by highlighting coding errors and performance issues.",
                "Execution begins from the main function, where control is first passed."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
        )
        val obj4 = Response("Gwen", 0, assessment_name,
            Answers = arrayListOf(
                "To serve as a working model that can be used to demonstrate features to stakeholders.",
                "Testing results can lead to changes in the system architecture and design.",
                "The main function is the starting point for execution in C++ programs."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
        )
        val obj5 = Response("Farhan", 0, assessment_name,
            Answers = arrayListOf(
                "To test and iterate on design and functionality before committing to full-scale development.",
                "The maintenance phase is influenced by testing through the identification of bugs and required updates.",
                "C++ programs initiate execution from the main() function."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
        )
        val obj6 = Response("Samdish", 0, assessment_name,
            Answers = arrayListOf(
                "To explore different solutions and evaluate their feasibility quickly.",
                "It can alter the deployment phase by uncovering issues that need to be resolved before release.",
                "The main() function serves as the entry point for C++ program execution."
            ),
            ObtainedMarks_per_Question = arrayListOf(0, 0, 0),
            plaig_Status = false,
            plaig_companion = null
        )
        Dbref.child(obj.studentname.toString() + " " + obj.assessment_name.toString()).setValue(obj)
        Dbref.child(obj1.studentname.toString() + " " + obj1.assessment_name.toString()).setValue(obj1)
        Dbref.child(obj2.studentname.toString() + " " + obj2.assessment_name.toString()).setValue(obj2)
        Dbref.child(obj3.studentname.toString() + " " + obj3.assessment_name.toString()).setValue(obj3)
        Dbref.child(obj4.studentname.toString() + " " + obj4.assessment_name.toString()).setValue(obj4)
        Dbref.child(obj5.studentname.toString() + " " + obj5.assessment_name.toString()).setValue(obj5)
        Dbref.child(obj6.studentname.toString() + " " + obj6.assessment_name.toString()).setValue(obj6)

    }

    fun searchElementByName(name: String, callback: (Assessment?) -> Unit) {
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Assessment")
        val query = database.child(name)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val assessment = dataSnapshot.getValue(Assessment::class.java)
                callback(assessment)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(null)
            }
        })
    }

    private fun loadAPIobjects()
    {
        var i = 0
        for (q in checked_assessment.Questions) {
            for (res in newArraylist){
                val student_name = res.studentname.toString()
                var answer : String = "N/A"
                if (res.Answers.size > i){
                    answer = res.Answers[i]
                }
                val obj = APIobject(student_name, q, answer, false)
                api_list.add(obj)
            }
            i += 1
        }
    }

    private fun gradeResponses(){
        val json = Json.encodeToString(api_list)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://192.168.172.73:8000/gradeResponses/")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS)
            .readTimeout(5000, TimeUnit.SECONDS) // Set read timeout to 30 seconds
            .writeTimeout(5000, TimeUnit.SECONDS) // Set write timeout to 30 seconds
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string()?.let {
                gradedresponses = parseStudentmarks(it)
                gradedresponses.forEach {
                    println("Student Name: ${it.studentName}, Answer: ${it.answer}, Score: ${it.score}")
                }
                setstudentgrades()
            }
        }
    }

    private fun setstudentgrades(){
        var i = 0
        var counter = 0
        for (q in checked_assessment.Questions){
            for (res in newArraylist){
                res.ObtainedMarks_per_Question[i] = gradedresponses[counter].score
                counter += 1
            }
            i += 1
        }

        saveedittedresponses()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                newRecyclerView.adapter = ResponsesAdapter(newArraylist, this@StudentResponses)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun PlaigCheck(){
        val json = Json.encodeToString(api_list)
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = json.toRequestBody(mediaType)

        val request = Request.Builder()
            .url("http://192.168.172.73:8080/upload/")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS)
            .readTimeout(5000, TimeUnit.SECONDS) // Set read timeout to 30 seconds
            .writeTimeout(5000, TimeUnit.SECONDS) // Set write timeout to 30 seconds
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            response.body?.string()?.let {
                studentNames = parseStudentNames(it)
                println(studentNames.joinToString(", "))
                setplaigstudents()
            }
        }
    }

    fun setplaigstudents()
    {
        var i = 0
        for (res in newArraylist){
            i = 0
            for (stu in studentNames){
                if (stu == res.studentname.toString())
                {
                    res.plaig_Status = true
                    if (i % 2 == 0){
                        res.plaig_companion = studentNames[i + 1]
                    }
                    else{
                        res.plaig_companion = studentNames[i - 1]
                    }
                }
                i += 1
            }
        }

        saveedittedresponses()
        GlobalScope.launch(Dispatchers.Main) {
            try {
                newRecyclerView.adapter = ResponsesAdapter(newArraylist, this@StudentResponses)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveedittedresponses()
    {
        val resref = FirebaseDatabase.getInstance().getReference("Responses")
        resref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val response = snapshot.getValue(Response::class.java)
                    if (response != null) {
                        // Check if response with same student name exists in newArrayList
                        val matchingResponse = newArraylist.find { it.studentname == response.studentname }
                        if (matchingResponse != null) {
                            // Update existing response with data from matchingResponse
                            snapshot.ref.setValue(matchingResponse)
                                .addOnSuccessListener {
                                    println("Response updated successfully for student: ${matchingResponse.studentname}")
                                }
                                .addOnFailureListener { e ->
                                    println("Error updating response for student: ${matchingResponse.studentname}. Error: $e")
                                }
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("Database read error: ${databaseError.message}")
            }
        })
    }


    fun parseStudentNames(jsonResponse: String): Array<String> {
        val response = Json.decodeFromString<ServerResponse>(jsonResponse)
        val namesList = mutableListOf<String>()

        for (pair in response.unique_pairs) {
            namesList.add(pair.studentName1)
            namesList.add(pair.studentName2)
        }

        return namesList.toTypedArray()
    }

    fun parseStudentmarks(jsonResponse: String): Array<ParsedResponse>{
        val response = Json.decodeFromString<ServerResponse2>(jsonResponse)
        val parsedList = mutableListOf<ParsedResponse>()

        for (pair in response.unique_trios) {
            parsedList.add(ParsedResponse(pair.studentName1, pair.studentName2, pair.prediction))
        }

        return parsedList.toTypedArray()
    }

    private suspend fun loadResponses(): ArrayList<Response> = withContext(Dispatchers.IO) {
        val studentMarksRef = FirebaseDatabase.getInstance().getReference("Responses")
        suspendCoroutine { continuation ->
            studentMarksRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val assessmentlist = ArrayList<Response>()
                    for (childSnapshot in snapshot.children) {
                        val mark = childSnapshot.getValue(Response::class.java)
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