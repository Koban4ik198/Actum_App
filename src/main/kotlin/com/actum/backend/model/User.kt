package com.actum.backend.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true)
    val login: String,

    val password: String,

    @Enumerated(EnumType.STRING)
    val role: Role
)