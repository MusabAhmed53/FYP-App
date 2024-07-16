package com.example.prototype

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InnerAdaptor(
    private val answers: List<String>,
    val marks: MutableList<Int>
) : RecyclerView.Adapter<InnerAdaptor.InnerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_answer, parent, false)
        return InnerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.answerTextView.text = answers[position]
        holder.marksEditText.setText(marks[position].toString())

        holder.marksEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val newMarks = s?.toString()?.toIntOrNull()
                if (newMarks != null) {
                    marks[position] = newMarks
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int {
        return answers.size
    }

    class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val answerTextView: TextView = itemView.findViewById(R.id.answerTextView)
        val marksEditText: EditText = itemView.findViewById(R.id.marksEditText)
    }
}
