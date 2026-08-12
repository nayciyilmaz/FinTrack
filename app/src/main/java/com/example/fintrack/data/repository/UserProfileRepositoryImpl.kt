package com.example.fintrack.data.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.mapper.UserProfileMapper
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.UpdateEmailRequestDto
import com.example.fintrack.data.remote.dto.UpdateNameRequestDto
import com.example.fintrack.data.remote.dto.UpdatePasswordRequestDto
import com.example.fintrack.domain.model.UserProfile
import com.example.fintrack.domain.repository.UserProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class UserProfileRepositoryImpl @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager,
    private val userProfileMapper: UserProfileMapper,
    private val networkErrorParser: NetworkErrorParser,
    @ApplicationContext private val context: Context
) : UserProfileRepository {

    override suspend fun getProfile(): Resource<UserProfile> {
        return try {
            val response = authService.getCurrentUser()
            Resource.Success(userProfileMapper.toUserProfile(response))
        } catch (e: HttpException) {
            Timber.e(e, "Get user profile failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback))
        } catch (e: Exception) {
            Timber.e(e, "Get user profile failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updateName(firstName: String, lastName: String): Resource<UserProfile> {
        return try {
            val response = authService.updateName(UpdateNameRequestDto(firstName = firstName, lastName = lastName))
            Resource.Success(userProfileMapper.toUserProfile(response))
        } catch (e: HttpException) {
            Timber.e(e, "Update user name failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback), fieldErrors = errorDto?.fieldErrors)
        } catch (e: Exception) {
            Timber.e(e, "Update user name failed")
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
            Timber.e(e, "Update user email failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback), fieldErrors = errorDto?.fieldErrors)
        } catch (e: Exception) {
            Timber.e(e, "Update user email failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }

    override suspend fun updatePassword(currentPassword: String, newPassword: String): Resource<UserProfile> {
        return try {
            val response = authService.updatePassword(
                UpdatePasswordRequestDto(currentPassword = currentPassword, newPassword = newPassword)
            )
            Resource.Success(userProfileMapper.toUserProfile(response))
        } catch (e: HttpException) {
            Timber.e(e, "Update user password failed")
            val errorDto = networkErrorParser.parse(e)
            Resource.Error(message = errorDto?.message ?: context.getString(R.string.error_generic_fallback), fieldErrors = errorDto?.fieldErrors)
        } catch (e: Exception) {
            Timber.e(e, "Update user password failed")
            Resource.Error(message = e.message ?: context.getString(R.string.error_generic_fallback))
        }
    }
}
