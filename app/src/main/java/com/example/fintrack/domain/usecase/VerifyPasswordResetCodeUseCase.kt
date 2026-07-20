package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.repository.AuthRepository

class VerifyPasswordResetCodeUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, code: String): Resource<String> =
        authRepository.verifyPasswordResetCode(email, code)
}
