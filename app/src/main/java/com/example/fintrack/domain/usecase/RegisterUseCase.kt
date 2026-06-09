package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<User> = authRepository.register(firstName, lastName, email, password)
}
