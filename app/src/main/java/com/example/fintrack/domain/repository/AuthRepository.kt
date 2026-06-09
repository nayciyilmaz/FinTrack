package com.example.fintrack.domain.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.domain.model.User

interface AuthRepository {
    suspend fun register(firstName: String, lastName: String, email: String, password: String): Resource<User>
}
