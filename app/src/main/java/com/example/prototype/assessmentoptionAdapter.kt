package com.example.prototype

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class assessmentoptionAdapter (private val assessmentlist: ArrayList<Assessment>) : RecyclerView.Adapter<assessmentoptionAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.popup_assessment_option, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return assessmentlist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = assessmentlist[position]
        holder.check.isChecked = currentitem.isChecked
        holder.check.setOnCheckedChangeListener{ _, isChecked ->
            currentitem.isChecked = isChecked
        }

        holder.bind(currentitem)
    }

    fun getCheckedItems(): List<Assessment> {
        return assessmentlist.filter { it.isChecked }
    }

    fun isAnyCheckboxChecked(): Boolean {
        return assessmentlist.any { it.isChecked }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val assessmentname: TextView = itemView.findViewById(R.id.textViewOption)
        val check: CheckBox = itemView.findViewById(R.id.radioButtonOption)

        fun bind(item: Assessment) {
            assessmentname.text = item.name.toString()
        }
    }
}
