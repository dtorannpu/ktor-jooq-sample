package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id: Int,
    val title: String,
    val description: String,
)

@Serializable
data class CreateTask(
    val title: String?,
    val description: String?,
)
