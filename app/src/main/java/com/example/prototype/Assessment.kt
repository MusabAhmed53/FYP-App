package com.example.prototype

data class Assessment(
    var name: Any? = null,
    var totalmarks: Any? = null,
    var courseId: Any? = null,
    var instructor_name: Any? = null,
    var isChecked: Boolean = false,
    var Questions: ArrayList<String> = ArrayList(),
    var Marks_per_Question: ArrayList<String> = ArrayList()
)
