package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.UpdateEmailRequestDto
import com.example.fintrack.data.remote.dto.UpdateNameRequestDto
import com.example.fintrack.data.remote.dto.UpdatePasswordRequestDto
import com.example.fintrack.data.remote.dto.UserProfileResponseDto
import com.example.fintrack.domain.model.UserProfile
import com.example.fintrack.domain.repository.UserProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager,
    private val json: Json,
    @ApplicationContext private val context: Context
) : UserProfileRepository {

    override suspend fun getProfile(): Resource<UserProfile> {
        return try {
            val response = authService.getCurrentUser()
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updateName(firstName: String, lastName: String): Resource<UserProfile> {
        return try {
            val response = authService.updateName(UpdateNameRequestDto(firstName = firstName, lastName = lastName))
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback), fieldErrors = errorDto?.fieldErrors)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updateEmail(email: String): Resource<String> {
        return try {
            val response = authService.updateEmail(UpdateEmailRequestDto(email = email))
            tokenManager.saveToken(response.token)
            tokenManager.saveRefreshToken(response.refreshToken)
            Resource.Success(response.email)
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback), fieldErrors = errorDto?.fieldErrors)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updatePassword(currentPassword: String, newPassword: String): Resource<UserProfile> {
        return try {
            val response = authService.updatePassword(
                UpdatePasswordRequestDto(currentPassword = currentPassword, newPassword = newPassword)
            )
            Resource.Success(response.toDomain())
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback), fieldErrors = errorDto?.fieldErrors)
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    private fun UserProfileResponseDto.toDomain() = UserProfile(
        firstName = firstName,
        lastName = lastName,
        email = email,
        createdAt = LocalDate.parse(createdAt.substring(0, 10)),
        passwordChangedAt = LocalDateTime.parse(passwordChangedAt)
    )
}
