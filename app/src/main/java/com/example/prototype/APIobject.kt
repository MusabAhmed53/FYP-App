package com.example.prototype
import kotlinx.serialization.Serializable

@Serializable
data class APIobject(
    val studentName: String,
    val question: String,
    val answer: String,
    val isplag:Boolean
)
