package com.example.prototype

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ResponsesAdapter(
    private val responseList: ArrayList<Response>,
    private val context: Context
) : RecyclerView.Adapter<ResponsesAdapter.MyViewHolder>() {

    private val lighterRedColor: Int by lazy {
        ContextCompat.getColor(context, R.color.lighter_red)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.response_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return responseList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = responseList[position]
        holder.assessmentName.text = currentItem.assessment_name.toString()
        holder.studentName.text = currentItem.studentname.toString()

        holder.itemView.findViewById<LinearLayout>(R.id.linearCourseCard).setBackgroundResource(R.drawable.rectangle_bg_black_900_26_radius_20)

        if (currentItem.plaig_Status) {
            holder.itemView.setBackgroundColor(lighterRedColor)
        }

        // Set up the inner RecyclerView
        val innerAdapter = InnerAdaptor(currentItem.Answers, currentItem.ObtainedMarks_per_Question)
        holder.innerRecyclerView.layoutManager = LinearLayoutManager(context)
        holder.innerRecyclerView.adapter = innerAdapter

        // Handle save button click
        holder.saveButton.setOnClickListener {
            responseList[position].ObtainedMarks_per_Question = innerAdapter.marks as ArrayList<Int>
            val resref = FirebaseDatabase.getInstance().getReference("Responses")
            resref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (snapshot in dataSnapshot.children) {
                        val response = snapshot.getValue(Response::class.java)
                        if (response != null) {
                            if (response.studentname == currentItem.studentname) {
                                snapshot.ref.setValue(currentItem)
                                    .addOnSuccessListener {
                                        println("Response updated successfully for student: ${currentItem.studentname}")
                                        Toast.makeText(context, "Marks updated", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        println("Error updating response for student: ${currentItem.studentname}. Error: $e")
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
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val assessmentName: TextView = itemView.findViewById(R.id.assessmentname)
        val studentName: TextView = itemView.findViewById(R.id.stuname)
        val innerRecyclerView: RecyclerView = itemView.findViewById(R.id.innerRecyclerView)
        val saveButton: Button = itemView.findViewById(R.id.saveButton)
    }
}
