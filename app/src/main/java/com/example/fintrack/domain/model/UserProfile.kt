package com.example.fintrack.domain.model

import java.time.LocalDate

data class UserProfile(
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: LocalDate
)
