package com.example.fintrack.domain.usecase

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.UserProfile
import com.example.fintrack.domain.repository.UserProfileRepository

class UpdateUserNameUseCase(private val userProfileRepository: UserProfileRepository) {
    suspend operator fun invoke(firstName: String, lastName: String): Resource<UserProfile> =
        userProfileRepository.updateName(firstName, lastName)
}
