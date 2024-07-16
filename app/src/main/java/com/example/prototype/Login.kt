package com.example.prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class Login : AppCompatActivity() {
    private lateinit var button_signup: Button
    private lateinit var button_login: Button
    private lateinit var database: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentuser: User
    private lateinit var loadingScreen: LoadingScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loginscreen)
        currentuser = User(null, null, null, null)
        firebaseAuth = FirebaseAuth.getInstance()
        loadingScreen = LoadingScreen(this)
        button_login = findViewById(R.id.btnLogInOne)
        button_signup = findViewById(R.id.btnSignUp)
        button_signup.setOnClickListener{
            onButtonSignupClick()
        }

        button_login.setOnClickListener{
            loadingScreen.startAlertDialog()
            onButtonLoginClick()
        }
    }
    private fun onButtonSignupClick() {
        val intent = Intent(this, Signup::class.java)
        startActivity(intent)
    }

    private fun onButtonLoginClick() {
        val e: EditText = findViewById(R.id.etEmail)
        val p: EditText = findViewById(R.id.etFrameTwenty)
        val email = e.text.toString()
        val password = p.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val user: FirebaseUser? = firebaseAuth.currentUser
                    val uid: String? = user?.uid
                    readData(uid)
//                    Log.d("name", currentuser.name.toString())
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readData(userid: String?) {
        database = FirebaseDatabase.getInstance().getReference("User")
        if (userid != null) {
            database.child(userid).get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    var email = dataSnapshot.child("email").value as String?
                    var name = dataSnapshot.child("name").value as String?
                    var password = dataSnapshot.child("password").value as String?
                    var userType = dataSnapshot.child("userType").value as String?
                    loadingScreen.closeAlertDialog()
                    if(userType == "Student"){
                        val intent = Intent(this, StudentStartup::class.java)
                        startActivity(intent)
                    }
                    if(userType == "Instructor")
                    {
                        val intent = Intent(this, InstructorStartup::class.java)
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this, "No record of this User", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to read User Data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
