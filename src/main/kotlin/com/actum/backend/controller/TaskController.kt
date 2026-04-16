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
import com.actum.backend.service.PdfService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/tasks")
class TaskController(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val reportRepository: ReportRepository,
    private val pdfService: PdfService
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
                clientPhone = request.clientPhone,
                priority = request.priority,
                deadline = request.deadline,
                status = TaskStatus.CREATED,
                manager = manager
            )
        )

        return mapToResponse(task)
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
        return mapToResponse(updated)
    }

    @PostMapping("/complete")
    fun completeTask(@RequestBody request: CompleteTaskRequest): TaskResponse {
        val task = taskRepository.findById(request.taskId)
            .orElseThrow { RuntimeException("Task not found") }

        if (task.status != TaskStatus.IN_PROGRESS) {
            throw RuntimeException("Task is not in progress")
        }

        task.status = TaskStatus.DONE
        val updated = taskRepository.save(task)

        val existingReport = reportRepository.findByTask_Id(request.taskId)
        if (existingReport.isPresent) {
            reportRepository.delete(existingReport.get())
        }

        reportRepository.save(
            Report(
                task = updated,
                data = request.data
            )
        )

        return mapToResponse(updated)
    }

    @PostMapping("/{id}/cancel")
    fun cancelTask(
        @PathVariable id: Long,
        @RequestParam reason: String
    ): TaskResponse {
        val task = taskRepository.findById(id)
            .orElseThrow { RuntimeException("Task not found") }

        if (task.status != TaskStatus.CREATED && task.status != TaskStatus.IN_PROGRESS) {
            throw RuntimeException("Task cannot be cancelled")
        }

        task.status = TaskStatus.CANCELLED
        val updated = taskRepository.save(task)

        val existingReport = reportRepository.findByTask_Id(id)
        if (existingReport.isEmpty) {
            reportRepository.save(
                Report(
                    task = updated,
                    data = """
                        {
                          "cancelReason":"$reason"
                        }
                    """.trimIndent()
                )
            )
        }

        return mapToResponse(updated)
    }

    @GetMapping("/{id}/report")
    fun getReport(@PathVariable id: Long): ReportResponse {
        val task = taskRepository.findById(id)
            .orElseThrow { RuntimeException("Task not found") }

        var report = reportRepository.findByTask_Id(id).orElse(null)

        if (report == null) {
            report = when (task.status) {
                TaskStatus.DONE -> {
                    reportRepository.save(
                        Report(
                            task = task,
                            data = """
                                {
                                  "workDone":"Работа выполнена",
                                  "client":"${task.clientName}",
                                  "result":"Успешно"
                                }
                            """.trimIndent()
                        )
                    )
                }

                TaskStatus.CANCELLED -> {
                    reportRepository.save(
                        Report(
                            task = task,
                            data = """
                                {
                                  "cancelReason":"Причина не была сохранена ранее"
                                }
                            """.trimIndent()
                        )
                    )
                }

                else -> throw RuntimeException("Report not found")
            }
        }

        return ReportResponse(
            taskId = report.task.id,
            data = report.data
        )
    }

    @GetMapping("/{id}/report/pdf")
    fun downloadReportPdf(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val task = taskRepository.findById(id)
            .orElseThrow { RuntimeException("Task not found") }

        var report = reportRepository.findByTask_Id(id).orElse(null)

        if (report == null) {
            report = when (task.status) {
                TaskStatus.DONE -> {
                    reportRepository.save(
                        Report(
                            task = task,
                            data = """
                                {
                                  "workDone":"Работа выполнена",
                                  "client":"${task.clientName}",
                                  "result":"Успешно"
                                }
                            """.trimIndent()
                        )
                    )
                }

                TaskStatus.CANCELLED -> {
                    reportRepository.save(
                        Report(
                            task = task,
                            data = """
                                {
                                  "cancelReason":"Причина не была сохранена ранее"
                                }
                            """.trimIndent()
                        )
                    )
                }

                else -> throw RuntimeException("Report not found")
            }
        }

        val pdfBytes = pdfService.generateTaskReportPdf(task, report)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=task-report-$id.pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdfBytes)
    }

    @GetMapping
    fun getAllTasks(): List<TaskResponse> {
        return taskRepository.findAll().map { mapToResponse(it) }
    }

    private fun mapToResponse(task: Task): TaskResponse {
        return TaskResponse(
            id = task.id,
            title = task.title,
            address = task.address,
            clientName = task.clientName,
            clientPhone = task.clientPhone,
            priority = task.priority,
            deadline = task.deadline,
            status = task.status,
            managerId = task.manager.id,
            specialistId = task.specialist?.id
        )
    }
}