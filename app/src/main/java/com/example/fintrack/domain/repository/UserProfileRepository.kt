package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(): Resource<UserProfile>
    suspend fun updateName(firstName: String, lastName: String): Resource<UserProfile>
    suspend fun updateEmail(email: String): Resource<String>
    suspend fun updatePassword(currentPassword: String, newPassword: String): Resource<UserProfile>
}
