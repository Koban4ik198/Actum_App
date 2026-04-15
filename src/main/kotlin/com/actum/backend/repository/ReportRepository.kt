package com.actum.backend.repository

import com.actum.backend.model.Report
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ReportRepository : JpaRepository<Report, Long> {

    fun findByTask_Id(taskId: Long): Optional<Report>
}