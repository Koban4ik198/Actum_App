package com.actum.backend.dto

data class CompleteTaskRequest(
    val taskId: Long,
    val data: String
)