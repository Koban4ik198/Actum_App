package com.actum.backend.repository

import com.actum.backend.model.Task
import com.actum.backend.model.TaskStatus
import org.springframework.data.jpa.repository.JpaRepository

interface TaskRepository : JpaRepository<Task, Long> {

    fun findByStatus(status: TaskStatus): List<Task>

    fun findBySpecialistId(specialistId: Long): List<Task>
}