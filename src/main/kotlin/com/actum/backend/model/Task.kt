package com.actum.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "tasks")
data class Task(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val title: String,

    val address: String,

    val clientName: String,

    val clientPhone: String? = null,

    val priority: String? = null,

    val deadline: String? = null,

    @Enumerated(EnumType.STRING)
    var status: TaskStatus = TaskStatus.CREATED,

    @ManyToOne
    @JoinColumn(name = "manager_id")
    val manager: User,

    @ManyToOne
    @JoinColumn(name = "specialist_id")
    var specialist: User? = null
)