package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.EmailUpdateResponseDto
import com.example.fintrack.data.remote.dto.ForgotPasswordRequestDto
import com.example.fintrack.data.remote.dto.GoogleAuthRequestDto
import com.example.fintrack.data.remote.dto.LoginRequestDto
import com.example.fintrack.data.remote.dto.LoginResponseDto
import com.example.fintrack.data.remote.dto.RegisterRequestDto
import com.example.fintrack.data.remote.dto.RegisterResponseDto
import com.example.fintrack.data.remote.dto.ResetPasswordRequestDto
import com.example.fintrack.data.remote.dto.UpdateEmailRequestDto
import com.example.fintrack.data.remote.dto.UpdateNameRequestDto
import com.example.fintrack.data.remote.dto.UpdatePasswordRequestDto
import com.example.fintrack.data.remote.dto.UserProfileResponseDto
import com.example.fintrack.data.remote.dto.VerifyResetCodeRequestDto
import com.example.fintrack.data.remote.dto.VerifyResetCodeResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): RegisterResponseDto

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @POST("api/auth/google")
    suspend fun loginWithGoogle(@Body request: GoogleAuthRequestDto): LoginResponseDto

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto)

    @POST("api/auth/verify-reset-code")
    suspend fun verifyResetCode(@Body request: VerifyResetCodeRequestDto): VerifyResetCodeResponseDto

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto)

    @GET("api/users/me")
    suspend fun getCurrentUser(): UserProfileResponseDto

    @POST("api/users/me/logout")
    suspend fun logout()

    @PUT("api/users/me/name")
    suspend fun updateName(@Body request: UpdateNameRequestDto): UserProfileResponseDto

    @PUT("api/users/me/email")
    suspend fun updateEmail(@Body request: UpdateEmailRequestDto): EmailUpdateResponseDto

    @PUT("api/users/me/password")
    suspend fun updatePassword(@Body request: UpdatePasswordRequestDto): UserProfileResponseDto
}
