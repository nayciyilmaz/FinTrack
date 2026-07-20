package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.repository.AuthRepository

class SendPasswordResetCodeUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String): Resource<Unit> =
        authRepository.sendPasswordResetCode(email)
}
