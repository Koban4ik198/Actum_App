package com.actum.backend.dto

data class CancelTaskRequest(
    val taskId: Long,
    val reason: String
)