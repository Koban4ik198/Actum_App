package com.actum.backend.dto

data class TaskResponse(
    val id: Long,
    val title: String,
    val address: String,
    val clientName: String,
    val clientPhone: String?,
    val priority: String?,
    val deadline: String?,
    val status: com.actum.backend.model.TaskStatus,
    val managerId: Long,
    val specialistId: Long?
)