package com.example.prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentMarksAdapter (private val assessmentlist: ArrayList<markTablerow>) : RecyclerView.Adapter<StudentMarksAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.tablestumarksrow, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return assessmentlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = assessmentlist[position]
        holder.Assessment_name.text=currentitem.assessmentname.toString()
        holder.Obtained_marks.text=currentitem.studentMarks.toString()
        holder.Total_marks.text=currentitem.TotalMarks.toString()
        holder.Mean.text=currentitem.MeanMarks.toString()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val Assessment_name: TextView=itemView.findViewById(R.id.Assessment_name)
        val Obtained_marks: TextView=itemView.findViewById(R.id.Obtained_marks)
        val Total_marks: TextView=itemView.findViewById(R.id.Total_marks)
        val Mean: TextView=itemView.findViewById(R.id.meanScore)
    }
}
