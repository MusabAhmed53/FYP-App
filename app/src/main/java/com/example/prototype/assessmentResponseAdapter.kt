package com.example.prototype

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope

class assessmentResponseAdapter(private val assessmentlist: ArrayList<Assessment>, private val context: Context) : RecyclerView.Adapter<assessmentResponseAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_studentmarks, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return assessmentlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = assessmentlist[position]
        holder.assessmentname.text = currentitem.name.toString()
        holder.coursename.text = currentitem.courseId.toString()
        holder.instructorname.text = currentitem.instructor_name.toString()
        holder.resBtn.setOnClickListener{
            val intent = Intent(context, StudentResponses::class.java)
            intent.putExtra("assessmentId", currentitem.name.toString())
            context.startActivity(intent)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val assessmentname: TextView = itemView.findViewById(R.id.txtCourseName)
        val instructorname: TextView = itemView.findViewById(R.id.txtInstructorName)
        val coursename: TextView = itemView.findViewById(R.id.txtAssessmentsCounter)
        val resBtn: Button = itemView.findViewById(R.id.btnViewMarks)
        fun bind(item: Assessment, context: Context) {
            assessmentname.text = item.name.toString()
            instructorname.text = item.instructor_name.toString()
            coursename.text = item.courseId.toString()
            resBtn.setOnClickListener {
                val intent = Intent(context, StudentResponses::class.java)
                intent.putExtra("assessmentId", item.name.toString())
                context.startActivity(intent)
            }
        }
    }
}