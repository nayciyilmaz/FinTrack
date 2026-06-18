package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.repository.AuthRepository

class GoogleSignInUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Resource<User> =
        authRepository.loginWithGoogle(idToken)
}
