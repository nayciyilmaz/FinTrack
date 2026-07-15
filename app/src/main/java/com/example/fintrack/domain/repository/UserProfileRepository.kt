package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.UserProfile

interface UserProfileRepository {
    suspend fun getProfile(): Resource<UserProfile>
}
