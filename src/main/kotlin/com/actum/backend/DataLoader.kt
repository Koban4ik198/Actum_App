package com.actum.backend

import com.actum.backend.model.Role
import com.actum.backend.model.User
import com.actum.backend.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataLoader {

    @Bean
    fun initUsers(userRepository: UserRepository) = CommandLineRunner {

        if (userRepository.count() == 0L) {

            userRepository.save(
                User(
                    login = "manager",
                    password = "1234",
                    role = Role.MANAGER
                )
            )

            userRepository.save(
                User(
                    login = "worker",
                    password = "1234",
                    role = Role.SPECIALIST
                )
            )
        }
    }
}