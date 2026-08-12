package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.UserProfileRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.mapper.UserProfileMapper
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.EmailUpdateResponseDto
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.UserProfileResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.UserProfile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime

class UserProfileRepositoryImplTest {

    private lateinit var authService: AuthService
    private lateinit var tokenManager: TokenManager
    private lateinit var userProfileMapper: UserProfileMapper
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: UserProfileRepositoryImpl

    @Before
    fun setUp() {
        authService = mockk()
        tokenManager = mockk(relaxed = true)
        userProfileMapper = mockk()
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        repository = UserProfileRepositoryImpl(authService, tokenManager, userProfileMapper, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    private fun buildProfileDto() = UserProfileResponseDto(
        firstName = "John", lastName = "Doe", email = "john@example.com",
        createdAt = "2026-01-01", passwordChangedAt = "2026-01-01T10:00:00"
    )

    private fun buildProfile() = UserProfile(
        firstName = "John", lastName = "Doe", email = "john@example.com",
        createdAt = LocalDate.of(2026, 1, 1), passwordChangedAt = LocalDateTime.of(2026, 1, 1, 10, 0)
    )

    @Test
    fun `getProfile returns mapped profile when api call succeeds`() = runTest {
        val dto = buildProfileDto()
        val profile = buildProfile()
        coEvery { authService.getCurrentUser() } returns dto
        every { userProfileMapper.toUserProfile(dto) } returns profile

        val result = repository.getProfile()

        assertTrue(result is Resource.Success)
        assertEquals("john@example.com", (result as Resource.Success).data?.email)
    }

    @Test
    fun `getProfile returns error when http exception thrown`() = runTest {
        val exception = httpException(401)
        coEvery { authService.getCurrentUser() } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.getProfile()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `updateName returns success when api call succeeds`() = runTest {
        val dto = buildProfileDto().copy(firstName = "Jane")
        val profile = buildProfile().copy(firstName = "Jane")
        coEvery { authService.updateName(any()) } returns dto
        every { userProfileMapper.toUserProfile(dto) } returns profile

        val result = repository.updateName("Jane", "Doe")

        assertTrue(result is Resource.Success)
        assertEquals("Jane", (result as Resource.Success).data?.firstName)
    }

    @Test
    fun `updateName returns error with field errors when validation fails`() = runTest {
        val exception = httpException(400)
        coEvery { authService.updateName(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(
            9000, "Validation failed", mapOf("first_name" to "Too short")
        )

        val result = repository.updateName("Jo", "Doe")

        assertTrue(result is Resource.Error)
        assertEquals("Too short", (result as Resource.Error).fieldErrors?.get("first_name"))
    }

    @Test
    fun `updateEmail saves new tokens and returns updated email when api call succeeds`() = runTest {
        val response = EmailUpdateResponseDto(email = "new@example.com", token = "new-token", refreshToken = "new-refresh-token")
        coEvery { authService.updateEmail(any()) } returns response

        val result = repository.updateEmail("new@example.com")

        assertTrue(result is Resource.Success)
        assertEquals("new@example.com", (result as Resource.Success).data)
        coVerify { tokenManager.saveToken("new-token") }
        coVerify { tokenManager.saveRefreshToken("new-refresh-token") }
    }

    @Test
    fun `updateEmail returns error when email already exists`() = runTest {
        val exception = httpException(409)
        coEvery { authService.updateEmail(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1002, "Email already exists")

        val result = repository.updateEmail("taken@example.com")

        assertTrue(result is Resource.Error)
        assertEquals("Email already exists", (result as Resource.Error).message)
    }

    @Test
    fun `updatePassword returns success when api call succeeds`() = runTest {
        val dto = buildProfileDto()
        val profile = buildProfile()
        coEvery { authService.updatePassword(any()) } returns dto
        every { userProfileMapper.toUserProfile(dto) } returns profile

        val result = repository.updatePassword("oldpassword", "newpassword123")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `updatePassword returns error when current password incorrect`() = runTest {
        val exception = httpException(401)
        coEvery { authService.updatePassword(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1003, "Incorrect password")

        val result = repository.updatePassword("wrongpassword", "newpassword123")

        assertTrue(result is Resource.Error)
        assertEquals("Incorrect password", (result as Resource.Error).message)
    }
}
