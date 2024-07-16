package com.example.prototype

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class ServerResponse2(
    val unique_trios: List<UniqueTrio>
)