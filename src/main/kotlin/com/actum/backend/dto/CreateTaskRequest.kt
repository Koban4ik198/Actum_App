package com.actum.backend.dto

data class CreateTaskRequest(
    val title: String,
    val address: String,
    val clientName: String,
    val managerId: Long
)