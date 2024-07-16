package com.example.prototype


import com.example.prototype.R
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class StudentProfileActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_studentprofile)


        nameEditText = findViewById(R.id.txtBilalAbassi)
        emailEditText = findViewById(R.id.txtEmailOne)
        val updateNameButton: Button = findViewById(R.id.btnEdit)
        val updateEmailButton: Button = findViewById(R.id.btnEditOne)

        firebaseAuth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = firebaseAuth.currentUser
        val uid: String? = user?.uid
        database = FirebaseDatabase.getInstance().getReference("User")

        // Retrieve and display current user data
        uid?.let {
            database.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val name = dataSnapshot.child("name").value as String?
                    val email = dataSnapshot.child("email").value as String?
                    nameEditText.setText(name)
                    emailEditText.setText(email)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                }
            })
        }

        // Update name button click listener
        updateNameButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            uid?.let { userId ->
                database.child(userId).child("name").setValue(newName)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Name updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to update name: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }

        updateEmailButton.setOnClickListener {

            val newEmail = emailEditText.text.toString().trim()
            uid?.let { userId ->
                database.child(userId).child("email").setValue(newEmail)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Email updated successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to update name: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

}