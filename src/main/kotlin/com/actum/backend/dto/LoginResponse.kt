package com.actum.backend.dto

data class LoginResponse(
    val token: String,
    val role: String,
    val userId: Long,
    val fullName: String
)