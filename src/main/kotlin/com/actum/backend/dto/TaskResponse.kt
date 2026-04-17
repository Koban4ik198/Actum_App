package com.actum.backend.dto

import com.actum.backend.model.TaskStatus

data class TaskResponse(
    val id: Long,
    val title: String,
    val address: String,
    val clientName: String,
    val clientPhone: String?,
    val priority: String?,
    val deadline: String?,
    val status: TaskStatus,
    val managerId: Long,
    val managerFullName: String,
    val specialistId: Long?,
    val specialistFullName: String?
)