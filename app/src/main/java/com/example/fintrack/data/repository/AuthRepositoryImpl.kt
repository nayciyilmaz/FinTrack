package com.example.fintrack.data.repository

import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.mapper.AuthMapper
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.GoogleAuthRequestDto
import com.example.fintrack.data.remote.dto.LoginRequestDto
import com.example.fintrack.data.remote.dto.RegisterRequestDto
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val authMapper: AuthMapper,
    private val tokenManager: TokenManager,
    private val json: Json
) : AuthRepository {

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Resource<User> {
        return try {
            val response = authService.register(
                RegisterRequestDto(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password
                )
            )
            tokenManager.saveToken(response.token)
            Resource.Success(authMapper.toUser(response))
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(
                message = errorDto?.message ?: "Bir hata oluştu",
                fieldErrors = errorDto?.fieldErrors
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = authService.login(LoginRequestDto(email = email, password = password))
            tokenManager.saveToken(response.token)
            Resource.Success(authMapper.toUser(response))
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            val fieldErrors = when (errorDto?.code) {
                1001 -> mapOf("email" to (errorDto.message))
                1003 -> mapOf("password" to (errorDto.message))
                else -> errorDto?.fieldErrors
            }
            Resource.Error(
                message = errorDto?.message ?: "Bir hata oluştu",
                fieldErrors = fieldErrors
            )
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val response = authService.loginWithGoogle(GoogleAuthRequestDto(idToken = idToken))
            tokenManager.saveToken(response.token)
            Resource.Success(authMapper.toUser(response))
        } catch (e: HttpException) {
            val errorDto = e.response()?.errorBody()?.string()?.let {
                runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
            }
            Resource.Error(message = errorDto?.message ?: "Google ile giriş başarısız")
        } catch (e: Exception) {
            Resource.Error(message = e.message ?: "Bir hata oluştu")
        }
    }
}
