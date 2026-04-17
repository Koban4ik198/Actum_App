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

    val lastName: String,

    val firstName: String,

    val middleName: String? = null,

    @Enumerated(EnumType.STRING)
    val role: Role
) {
    fun getFullName(): String {
        return listOfNotNull(lastName, firstName, middleName)
            .joinToString(" ")
    }
}