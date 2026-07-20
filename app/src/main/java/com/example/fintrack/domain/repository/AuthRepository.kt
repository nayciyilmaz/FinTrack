package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.User

interface AuthRepository {
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        payday: Int,
        salary: Double
    ): Resource<User>

    suspend fun login(email: String, password: String): Resource<User>
    suspend fun loginWithGoogle(idToken: String): Resource<User>
    suspend fun logout(): Resource<Unit>
    suspend fun sendPasswordResetCode(email: String): Resource<Unit>
    suspend fun verifyPasswordResetCode(email: String, code: String): Resource<String>
    suspend fun resetPassword(resetToken: String, newPassword: String): Resource<Unit>
}
