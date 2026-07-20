package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.repository.AuthRepository

class ResetPasswordUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(resetToken: String, newPassword: String): Resource<Unit> =
        authRepository.resetPassword(resetToken, newPassword)
}
