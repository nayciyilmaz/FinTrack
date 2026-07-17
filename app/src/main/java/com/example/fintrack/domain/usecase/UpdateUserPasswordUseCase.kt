package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.UserProfile
import com.example.fintrack.domain.repository.UserProfileRepository

class UpdateUserPasswordUseCase(private val userProfileRepository: UserProfileRepository) {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Resource<UserProfile> =
        userProfileRepository.updatePassword(currentPassword, newPassword)
}
