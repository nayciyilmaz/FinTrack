package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Resource<User> =
        authRepository.login(email, password)
}
