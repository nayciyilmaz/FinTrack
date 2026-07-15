package com.example.fintrack.data.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.UserProfileResponseDto
import com.example.fintrack.domain.model.UserProfile
import com.example.fintrack.domain.repository.UserProfileRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val json: Json
) : UserProfileRepository {

    override suspend fun getProfile(): Resource<UserProfile> {
        return try {
            val response = authService.getCurrentUser()
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: "Bir hata oluştu")
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    private fun UserProfileResponseDto.toDomain() = UserProfile(
        firstName = firstName,
        lastName = lastName,
        email = email,
        createdAt = LocalDate.parse(createdAt.substring(0, 10))
    )
}
