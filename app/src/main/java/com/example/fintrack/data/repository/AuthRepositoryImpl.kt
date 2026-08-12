package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.mapper.AuthMapper
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.ForgotPasswordRequestDto
import com.example.fintrack.data.remote.dto.GoogleAuthRequestDto
import com.example.fintrack.data.remote.dto.LoginRequestDto
import com.example.fintrack.data.remote.dto.RegisterRequestDto
import com.example.fintrack.data.remote.dto.ResetPasswordRequestDto
import com.example.fintrack.data.remote.dto.VerifyResetCodeRequestDto
import com.example.fintrack.domain.model.User
import com.example.fintrack.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val authMapper: AuthMapper,
    private val tokenManager: TokenManager,
    private val networkErrorParser: NetworkErrorParser,
    @ApplicationContext private val context: Context
) : AuthRepository {

    override suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        payday: Int,
        salary: Double
    ): Resource<User> {
        return try {
            val response = authService.register(
                RegisterRequestDto(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    payday = payday,
                    salary = salary
                )
            )
            tokenManager.saveToken(response.token)
            tokenManager.saveRefreshToken(response.refreshToken)
            tokenManager.savePayday(response.payday)
            Resource.Success(authMapper.toUser(response))
        } catch (e: HttpException) {
            Timber.e(e, "Register failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(
                message = errorDto?.message ?: context.getString(R.string.error_generic_fallback),
                fieldErrors = errorDto?.fieldErrors
            )
        } catch (e: Exception) {
            Timber.e(e, "Register failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = authService.login(LoginRequestDto(email = email, password = password))
            tokenManager.saveToken(response.token)
            tokenManager.saveRefreshToken(response.refreshToken)
            tokenManager.savePayday(response.payday)
            Resource.Success(authMapper.toUser(response))
        } catch (e: HttpException) {
            Timber.e(e, "Login failed")
            val errorDto = networkErrorParser.parse(e)
            val fieldErrors = when (errorDto?.code) {
                1001 -> mapOf("email" to (errorDto.message))
                1003 -> mapOf("password" to (errorDto.message))
                else -> errorDto?.fieldErrors
            }
            Resource.Error(
                message = errorDto?.message ?: context.getString(R.string.error_generic_fallback),
                fieldErrors = fieldErrors
            )
        } catch (e: Exception) {
            Timber.e(e, "Login failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Resource<User> {
        return try {
            val response = authService.loginWithGoogle(GoogleAuthRequestDto(idToken = idToken))
            tokenManager.saveToken(response.token)
            tokenManager.saveRefreshToken(response.refreshToken)
            tokenManager.savePayday(response.payday)
            Resource.Success(authMapper.toUser(response))
        } catch (e: HttpException) {
            Timber.e(e, "Google login failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_google_signin_failed))
        } catch (e: Exception) {
            Timber.e(e, "Google login failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun logout(): Resource<Unit> {
        try {
            authService.logout()
        } catch (e: Exception) {
            Timber.e(e, "Logout request failed")
        }
        tokenManager.clearAll()
        tokenManager.notifySessionExpired()
        return Resource.Success(Unit)
    }

    override suspend fun sendPasswordResetCode(email: String): Resource<Unit> {
        return try {
            authService.forgotPassword(ForgotPasswordRequestDto(email = email))
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Timber.e(e, "Send password reset code failed")
            val errorDto = networkErrorParser.parse(e)
            val fieldErrors = when (errorDto?.code) {
                1001 -> mapOf("email" to errorDto.message)
                else -> errorDto?.fieldErrors
            }
            Resource.Error(
                message = errorDto?.message ?: context.getString(R.string.error_generic_fallback),
                fieldErrors = fieldErrors
            )
        } catch (e: Exception) {
            Timber.e(e, "Send password reset code failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun verifyPasswordResetCode(email: String, code: String): Resource<String> {
        return try {
            val response = authService.verifyResetCode(VerifyResetCodeRequestDto(email = email, code = code))
            Resource.Success(response.resetToken)
        } catch (e: HttpException) {
            Timber.e(e, "Verify password reset code failed")
            val errorDto = networkErrorParser.parse(e)
            val fieldErrors = when (errorDto?.code) {
                1009 -> mapOf("code" to errorDto.message)
                else -> errorDto?.fieldErrors
            }
            Resource.Error(
                message = errorDto?.message ?: context.getString(R.string.error_generic_fallback),
                fieldErrors = fieldErrors
            )
        } catch (e: Exception) {
            Timber.e(e, "Verify password reset code failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun resetPassword(resetToken: String, newPassword: String): Resource<Unit> {
        return try {
            authService.resetPassword(ResetPasswordRequestDto(resetToken = resetToken, newPassword = newPassword))
            Resource.Success(Unit)
        } catch (e: HttpException) {
            Timber.e(e, "Reset password failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(
                message = errorDto?.message ?: context.getString(R.string.error_generic_fallback),
                fieldErrors = errorDto?.fieldErrors
            )
        } catch (e: Exception) {
            Timber.e(e, "Reset password failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }
}
