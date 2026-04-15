package com.actum.backend.dto

import com.actum.backend.model.TaskStatus

data class TaskResponse(
    val id: Long,
    val title: String,
    val address: String,
    val clientName: String,
    val status: TaskStatus,
    val managerId: Long,
    val specialistId: Long?
)