package com.example.fintrack.data.mapper

import com.example.fintrack.data.remote.dto.RegisterResponseDto
import com.example.fintrack.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthMapper @Inject constructor() {
    fun toUser(dto: RegisterResponseDto): User = User(
        id = dto.id,
        firstName = dto.firstName,
        lastName = dto.lastName,
        email = dto.email,
        token = dto.token
    )
}
