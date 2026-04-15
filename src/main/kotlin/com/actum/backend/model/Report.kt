package com.actum.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reports")
data class Report(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @OneToOne
    @JoinColumn(name = "task_id")
    val task: Task,

    @Column(columnDefinition = "TEXT")
    val data: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)