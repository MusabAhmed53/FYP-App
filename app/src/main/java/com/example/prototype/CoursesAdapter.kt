package com.example.prototype

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class CoursesAdapter(private val courselist: ArrayList<Course>) : RecyclerView.Adapter<CoursesAdapter.MyViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.row_course, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return courselist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = courselist[position]
        holder.coursename.text = currentitem.name.toString()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val coursename: TextView = itemView.findViewById(R.id.txtCourseName)
        init {
            itemView.setOnClickListener{
                val context = itemView.context
                val intent = Intent(itemView.context, CourseMarksDashboard::class.java)
                intent.putExtra("Course", coursename.text.toString())
                context.startActivity(intent)
            }
        }
        fun bind(item: Course) {
            coursename.text = item.name.toString()
        }
    }
}