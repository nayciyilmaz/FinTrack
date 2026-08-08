package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.UserProfileResponseDto
import com.example.fintrack.domain.model.UserProfile
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileMapper @Inject constructor() {
    fun toUserProfile(dto: UserProfileResponseDto): UserProfile = UserProfile(
        firstName = dto.firstName,
        lastName = dto.lastName,
        email = dto.email,
        createdAt = LocalDate.parse(dto.createdAt.substring(0, 10)),
        passwordChangedAt = LocalDateTime.parse(dto.passwordChangedAt)
    )
}
