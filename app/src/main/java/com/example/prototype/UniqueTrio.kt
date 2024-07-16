package com.example.prototype
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class UniqueTrio(
    val studentName1: String,
    val studentName2: String,
    val prediction: Int
)