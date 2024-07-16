package com.example.prototype

data class Response (
    var studentname: Any? = null,
    var obtainedmarks: Any? = null,
    var assessment_name: Any? = null,
    var Answers: ArrayList<String> = ArrayList(),
    var ObtainedMarks_per_Question: ArrayList<Int> = ArrayList(),
    var plaig_Status: Boolean = false,
    var plaig_companion: Any? = null
)