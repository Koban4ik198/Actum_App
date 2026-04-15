package com.actum.backend.controller

import com.actum.backend.dto.CompleteTaskRequest
import com.actum.backend.dto.CreateTaskRequest
import com.actum.backend.dto.ReportResponse
import com.actum.backend.dto.TaskResponse
import com.actum.backend.model.Report
import com.actum.backend.model.Task
import com.actum.backend.model.TaskStatus
import com.actum.backend.repository.ReportRepository
import com.actum.backend.repository.TaskRepository
import com.actum.backend.repository.UserRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository
) {

    @PostMapping
    fun createTask(@RequestBody request: CreateTaskRequest): TaskResponse {
        val manager = userRepository.findById(request.managerId)
            .orElseThrow { RuntimeException("Manager not found") }

        val task = taskRepository.save(
            Task(
                title = request.title,
                address = request.address,
                clientName = request.clientName,
                status = TaskStatus.CREATED,
                manager = manager
            )
        )

        return TaskResponse(
            id = task.id,
            title = task.title,
            address = task.address,
            clientName = task.clientName,
            status = task.status,
            managerId = task.manager.id,
            specialistId = task.specialist?.id
        )
    }

    @PostMapping("/{id}/take")
    fun takeTask(
        @PathVariable id: Long,
        @RequestParam specialistId: Long
    ): TaskResponse {

        val task = taskRepository.findById(id)
            .orElseThrow { RuntimeException("Task not found") }

        if (task.status != TaskStatus.CREATED) {
            throw RuntimeException("Task is not available")
        }

        val specialist = userRepository.findById(specialistId)
            .orElseThrow { RuntimeException("Specialist not found") }

        task.status = TaskStatus.IN_PROGRESS
        task.specialist = specialist

        val updated = taskRepository.save(task)

        return TaskResponse(
            id = updated.id,
            title = updated.title,
            address = updated.address,
            clientName = updated.clientName,
            status = updated.status,
            managerId = updated.manager.id,
            specialistId = updated.specialist?.id
        )
    }

    @PostMapping("/complete")
    fun completeTask(@RequestBody request: CompleteTaskRequest): TaskResponse {
        val task = taskRepository.findById(request.taskId)
            .orElseThrow { RuntimeException("Task not found") }

        if (task.status != TaskStatus.IN_PROGRESS) {
            throw RuntimeException("Task is not in progress")
        }

        reportRepository.save(
            Report(
                task = task,
                data = request.data
            )
        )

        task.status = TaskStatus.DONE
        val updated = taskRepository.save(task)

        return TaskResponse(
            id = updated.id,
            title = updated.title,
            address = updated.address,
            clientName = updated.clientName,
            status = updated.status,
            managerId = updated.manager.id,
            specialistId = updated.specialist?.id
        )
    }

    @GetMapping("/{id}/report")
    fun getReport(@PathVariable id: Long): ReportResponse {
        val report = reportRepository.findByTask_Id(id)
            .orElseThrow { RuntimeException("Report not found") }

        return ReportResponse(
            taskId = report.task.id,
            data = report.data
        )
    }

    @GetMapping
    fun getAllTasks(): List<TaskResponse> {
        return taskRepository.findAll().map { task ->
            TaskResponse(
                id = task.id,
                title = task.title,
                address = task.address,
                clientName = task.clientName,
                status = task.status,
                managerId = task.manager.id,
                specialistId = task.specialist?.id
            )
        }
    }
}