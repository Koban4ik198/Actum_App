package com.actum.backend.repository

import com.actum.backend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User, Long> {
    fun findByLogin(login: String): Optional<User>
}