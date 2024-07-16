package com.example.prototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databasereference: DatabaseReference
    private lateinit var databasereference2: DatabaseReference
    private lateinit var checkBoxInstructor: CheckBox
    private lateinit var checkBoxStudent: CheckBox
    private lateinit var button_signup: Button
    private lateinit var button_login: Button
    private lateinit var Email: EditText
    private lateinit var password: EditText
    private lateinit var Name: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signupscreen)

        firebaseAuth = FirebaseAuth.getInstance()
        databasereference = FirebaseDatabase.getInstance().getReference("User")
        button_login = findViewById(R.id.btnLogIn)
        button_signup = findViewById(R.id.btnSignUpOne)
        button_login.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        button_signup.setOnClickListener {
            Email = findViewById(R.id.etEmail)
            password = findViewById(R.id.etFrameTwenty)
            Name = findViewById(R.id.etLanguageTwo)
            val email = Email.text.toString().trim()
            val pass = password.text.toString().trim()
            val name = Name.text.toString().trim()
            checkBoxInstructor = findViewById<CheckBox>(R.id.checkBoxInstructor)
            checkBoxStudent = findViewById<CheckBox>(R.id.checkBoxStudent)

            if (email.isNotEmpty() && pass.isNotEmpty() && name.isNotEmpty()) {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        val uid: String? = user?.uid
                        databasereference2 = FirebaseDatabase.getInstance().getReference("Courses$uid")
                        if (uid != null) {
                            val userType: String = if (checkBoxInstructor.isChecked) {
                                "Instructor"
                            } else if (checkBoxStudent.isChecked) {
                                "Student"
                            } else {
                                "undefined"
                            }

                            if (userType == "Instructor")
                            {
                                val ass1 = Course("History 101", name, uid)
                                val ass2 = Course("English Lit", name, uid)
                                val ass3 = Course("Geography Year1", name, uid)
                                databasereference2.child(ass1.name.toString()).setValue(ass1)
                                databasereference2.child(ass2.name.toString()).setValue(ass2)
                                databasereference2.child(ass3.name.toString()).setValue(ass3)
                            }

                            if (userType == "undefined") {
                                Toast.makeText(this, "Please choose a user type", Toast.LENGTH_SHORT).show()
                            } else {
                                val newUser = User(name, email, pass, userType)
                                databasereference.child(uid).setValue(newUser).addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "Sign up successful", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, Login::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this, "Failed to add user to database", Toast.LENGTH_SHORT).show()
                                        Log.e("DatabaseError", dbTask.exception?.message ?: "Unknown error")
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, "Failed to retrieve user ID", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("SignUpError", task.exception?.message ?: "Unknown error")
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
