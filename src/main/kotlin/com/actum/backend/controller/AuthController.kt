package com.actum.backend.controller

import com.actum.backend.dto.LoginRequest
import com.actum.backend.dto.LoginResponse
import com.actum.backend.repository.UserRepository
import com.actum.backend.security.JwtService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtService: JwtService
) {

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): LoginResponse {
        val user = userRepository.findByLogin(request.login)
            .orElseThrow { RuntimeException("User not found") }

        if (user.password != request.password) {
            throw RuntimeException("Invalid password")
        }

        val token = jwtService.generateToken(user.login)

        return LoginResponse(
            token = token,
            role = user.role.name
        )
    }
}