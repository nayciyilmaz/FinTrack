package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.repository.UserProfileRepository

class UpdateUserEmailUseCase(private val userProfileRepository: UserProfileRepository) {
    suspend operator fun invoke(email: String): Resource<String> =
        userProfileRepository.updateEmail(email)
}
