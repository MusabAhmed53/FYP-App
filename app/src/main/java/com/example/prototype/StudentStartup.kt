package com.example.prototype

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.MultipartBody
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.RequestBody.Companion.toRequestBody

private const val Request_Code=42
private const val FILE_NAME= "photo.jpg"

private lateinit var photoFile: File
private lateinit var scannerLauncher: ActivityResultLauncher<IntentSenderRequest>

class StudentStartup : AppCompatActivity() {
    var myDialog: Dialog? = null
    private lateinit var newRecyclerView: RecyclerView
    private lateinit var newArraylist: ArrayList<Assessment>
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var checked_assessment: Assessment
    private var result: String? = null
    private lateinit var studentname: TextView
    private lateinit var home_btn:ImageView
    private lateinit var marks_btn : ImageView
    private lateinit var loadingScreen: LoadingScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_startpage)
        scannerLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            handleScanResult(result)
        }

        marks_btn = findViewById(R.id.imageMarks)
        loadingScreen = LoadingScreen(this)
        marks_btn.setOnClickListener{
            val intent = Intent(this, StudentPersonalMarks::class.java)
            startActivity(intent)
        }
        var btn_logout :ImageView = findViewById(R.id.imageMegaphone)

        btn_logout.setOnClickListener {
            showLogoutPopup()
        }

        var btn_profile:ImageView = findViewById(R.id.imageLock)
        btn_profile.setOnClickListener {
            val intent = Intent(this, StudentProfileActivity::class.java)
            startActivity(intent)
        }

        firebaseAuth = FirebaseAuth.getInstance()
        myDialog = Dialog(this);
        newArraylist = arrayListOf<Assessment>()
        val user: FirebaseUser? = firebaseAuth.currentUser
        val uid: String? = user?.uid
        home_btn=findViewById(R.id.imageHome)
        studentname = findViewById(R.id.txtStudentname)
        database = FirebaseDatabase.getInstance().getReference("User")
        if (uid != null) {
            database.child(uid).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    var email = dataSnapshot.child("email").value as String?
                    var name = dataSnapshot.child("name").value as String?
                    var password = dataSnapshot.child("password").value as String?
                    var userType = dataSnapshot.child("userType").value as String?
                    studentname.text = name

                } else {
                    Toast.makeText(this, "No record of this User", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to read User Data", Toast.LENGTH_SHORT).show()
            }
        }
        GlobalScope.launch(Dispatchers.Main) {
            try {
                newArraylist = loadAssessments()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showLogoutPopup() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.activity_logout, null)

        val btnLogout = dialogLayout.findViewById<Button>(R.id.btnLogout)

        builder.setView(dialogLayout)
        val alertDialog = builder.create()

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, Login::class.java))
            finish() // Close the current activity
        }

        alertDialog.show()
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

    fun showPopup(v: View) {
        val txtClose: TextView
        val btnNext: Button
        if(myDialog != null){
            myDialog!!.setContentView(R.layout.custom_popup)
            btnNext = myDialog!!.findViewById(R.id.btnnext)
            btnNext.setOnClickListener{
                val list_checked_assessments = getCheckedItems(newArraylist)
                checked_assessment = list_checked_assessments.firstOrNull()!!
                startScanner()
            }
            txtClose = myDialog!!.findViewById(R.id.txtclose)
            txtClose.setOnClickListener {
                myDialog!!.dismiss()
            }
            myDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            myDialog!!.show()
            newRecyclerView = myDialog!!.findViewById(R.id.recyclerViewPopupOptions)
            newRecyclerView.layoutManager = LinearLayoutManager(this)
            newRecyclerView.setHasFixedSize(true)
            newRecyclerView.addItemDecoration(SimpleItemDecoration(this))
            getUserData()
        }
    }

    fun getCheckedItems(itemList: List<Assessment>): List<Assessment> {
        return itemList.filter { it.isChecked }
    }

    fun startScanner(){
        val options = GmsDocumentScannerOptions.Builder()
            .setGalleryImportAllowed(true)
            .setPageLimit(1)
            .setResultFormats(RESULT_FORMAT_JPEG)
            .setScannerMode(SCANNER_MODE_FULL)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)
        scanner.getStartScanIntent(this)
            .addOnSuccessListener { intentSender ->
                val intentSenderRequest = IntentSenderRequest.Builder(intentSender).build()
                scannerLauncher.launch(intentSenderRequest)
            }
            .addOnFailureListener { exception ->
                println(exception)
            }
    }

    private fun handleScanResult(result: ActivityResult) {
        if (result.resultCode == RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
            result?.pages?.let { pages ->
                for (page in pages) {
                    val imageUri = page.getImageUri()
                    val imageFile = imageUri.path?.let { File(it) }
                    if (imageFile != null) {
                        photoFile=imageFile
                    }

                    if (imageFile != null) {
                        FileProvider.getUriForFile(this, "com.handwrittenautograder.fileprovider", imageFile)
                    }
                    GlobalScope.launch(Dispatchers.Main) {
                        try {

                            loadingScreen.startAlertDialog()

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    CoroutineScope(Default).launch {
                        OCR(photoFile)
                    }
                }
            }
        }
    }

    fun OCR(imageFile:File)
    {
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name, imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("http://192.168.172.73:8000/imageToText/")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS) // Set connection timeout to 30 seconds
            .readTimeout(5000, TimeUnit.SECONDS) // Set read timeout to 30 seconds
            .writeTimeout(5000, TimeUnit.SECONDS) // Set write timeout to 30 seconds
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            result = response.body?.string()
            Log.d("Response: ", result.toString())

            CoroutineScope(Dispatchers.Main).launch {

                GlobalScope.launch(Dispatchers.Main) {
                    try {

                        loadingScreen.startAlertDialog()

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                showEditTextDialogue()
            }
        }
    }

    private fun showEditTextDialogue()
    {
        val builder = AlertDialog.Builder(this@StudentStartup)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.edit_text_layout, null)
        var responsetext = dialogLayout.findViewById<EditText>(R.id.responsetext)
        responsetext.setText(result.toString())

        with(builder){
            setTitle("Your Response")
            setPositiveButton("Submit"){dialog, which->
                val name = studentname.text
                val assessment_name = checked_assessment.name
                val responses : String = responsetext.text.toString()
                val answers : ArrayList<String> = arrayListOf()
                val responses_list : List<String> = responses.split("Question:")
                val newlist = responses_list.drop(1)
                for (res in newlist)
                {
                    val ele : List<String> = res.split("Answer:")
                    Log.d("Response", ele[1])
                    answers.add(ele[1])
                }
                val obtained_marks_per_question: ArrayList<Int> = arrayListOf()
                var i = 0
                while (i < answers.size){
                    obtained_marks_per_question.add(0)
                    i += 1
                }
                val re = Response(name, 0, assessment_name, answers, obtained_marks_per_question, false, null)
                database = FirebaseDatabase.getInstance().getReference("Responses")
                database.child(re.studentname.toString() + " " + re.assessment_name.toString()).setValue(re).addOnCompleteListener { dbTask ->
                    if (dbTask.isSuccessful) {
                        Toast.makeText(this@StudentStartup, "Response is submitted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@StudentStartup, "Response could not be saved", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Cancel"){dialog, which->
                Toast.makeText(this@StudentStartup, "Response is discarded", Toast.LENGTH_SHORT).show()
            }
            setView(dialogLayout)
            show()
        }
    }

    private fun getUserData()
    {
        newRecyclerView.adapter = assessmentoptionAdapter(newArraylist)
    }

}