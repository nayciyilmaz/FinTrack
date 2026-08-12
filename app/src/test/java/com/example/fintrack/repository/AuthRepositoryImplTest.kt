package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.AuthRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.mapper.AuthMapper
import com.example.fintrack.data.remote.api.AuthService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.LoginResponseDto
import com.example.fintrack.data.remote.dto.RegisterResponseDto
import com.example.fintrack.data.remote.dto.VerifyResetCodeResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.User
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class AuthRepositoryImplTest {

    private lateinit var authService: AuthService
    private lateinit var authMapper: AuthMapper
    private lateinit var tokenManager: TokenManager
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: AuthRepositoryImpl

    @Before
    fun setUp() {
        authService = mockk()
        authMapper = mockk()
        tokenManager = mockk(relaxed = true)
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        every { context.getString(R.string.error_google_signin_failed) } returns "google error"
        repository = AuthRepositoryImpl(authService, authMapper, tokenManager, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    private fun buildLoginResponse() = LoginResponseDto(
        id = 1L, firstName = "John", lastName = "Doe", email = "john@example.com",
        payday = 5, token = "access-token", refreshToken = "refresh-token"
    )

    private fun buildUser() = User(
        id = 1L, firstName = "John", lastName = "Doe", email = "john@example.com",
        payday = 5, token = "access-token"
    )

    @Test
    fun `register returns success and saves tokens when api call succeeds`() = runTest {
        val response = RegisterResponseDto(
            id = 1L, firstName = "John", lastName = "Doe", email = "john@example.com",
            payday = 5, token = "access-token", refreshToken = "refresh-token"
        )
        coEvery { authService.register(any()) } returns response
        every { authMapper.toUser(response) } returns buildUser()

        val result = repository.register("John", "Doe", "john@example.com", "password123", 5, 1000.0)

        assertTrue(result is Resource.Success)
        assertEquals("john@example.com", (result as Resource.Success).data?.email)
        coVerify { tokenManager.saveToken("access-token") }
        coVerify { tokenManager.saveRefreshToken("refresh-token") }
        coVerify { tokenManager.savePayday(5) }
    }

    @Test
    fun `register returns error with parsed message when http exception thrown`() = runTest {
        val exception = httpException(409)
        coEvery { authService.register(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1002, "Email already exists")

        val result = repository.register("John", "Doe", "john@example.com", "password123", 5, 1000.0)

        assertTrue(result is Resource.Error)
        assertEquals("Email already exists", (result as Resource.Error).message)
    }

    @Test
    fun `register returns fallback message when error body cannot be parsed`() = runTest {
        val exception = httpException(500)
        coEvery { authService.register(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.register("John", "Doe", "john@example.com", "password123", 5, 1000.0)

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `login returns success when credentials valid`() = runTest {
        val response = buildLoginResponse()
        coEvery { authService.login(any()) } returns response
        every { authMapper.toUser(response) } returns buildUser()

        val result = repository.login("john@example.com", "password123")

        assertTrue(result is Resource.Success)
        coVerify { tokenManager.saveToken("access-token") }
    }

    @Test
    fun `login maps error code 1001 to email field error`() = runTest {
        val exception = httpException(404)
        coEvery { authService.login(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1001, "User not found")

        val result = repository.login("missing@example.com", "password123") as Resource.Error

        assertEquals("User not found", result.fieldErrors?.get("email"))
    }

    @Test
    fun `login maps error code 1003 to password field error`() = runTest {
        val exception = httpException(401)
        coEvery { authService.login(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1003, "Incorrect password")

        val result = repository.login("john@example.com", "wrongpass") as Resource.Error

        assertEquals("Incorrect password", result.fieldErrors?.get("password"))
    }

    @Test
    fun `login returns generic error when unexpected exception thrown`() = runTest {
        coEvery { authService.login(any()) } throws RuntimeException("network down")

        val result = repository.login("john@example.com", "password123") as Resource.Error

        assertEquals("network down", result.message)
    }

    @Test
    fun `loginWithGoogle returns success when token valid`() = runTest {
        val response = buildLoginResponse()
        coEvery { authService.loginWithGoogle(any()) } returns response
        every { authMapper.toUser(response) } returns buildUser()

        val result = repository.loginWithGoogle("google-id-token")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `loginWithGoogle returns google specific fallback message when parser returns null`() = runTest {
        val exception = httpException(401)
        coEvery { authService.loginWithGoogle(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.loginWithGoogle("bad-token") as Resource.Error

        assertEquals("google error", result.message)
    }

    @Test
    fun `logout clears tokens and notifies session expired even when api call fails`() = runTest {
        coEvery { authService.logout() } throws RuntimeException("timeout")
        coEvery { tokenManager.clearAll() } just Runs

        val result = repository.logout()

        assertTrue(result is Resource.Success)
        coVerify { tokenManager.clearAll() }
        coVerify { tokenManager.notifySessionExpired() }
    }

    @Test
    fun `logout clears tokens when api call succeeds`() = runTest {
        coEvery { authService.logout() } just Runs
        coEvery { tokenManager.clearAll() } just Runs

        val result = repository.logout()

        assertTrue(result is Resource.Success)
        coVerify { tokenManager.clearAll() }
    }

    @Test
    fun `sendPasswordResetCode returns success when email valid`() = runTest {
        coEvery { authService.forgotPassword(any()) } just Runs

        val result = repository.sendPasswordResetCode("john@example.com")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `sendPasswordResetCode maps error code 1001 to email field error`() = runTest {
        val exception = httpException(404)
        coEvery { authService.forgotPassword(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1001, "User not found")

        val result = repository.sendPasswordResetCode("missing@example.com") as Resource.Error

        assertEquals("User not found", result.fieldErrors?.get("email"))
    }

    @Test
    fun `verifyPasswordResetCode returns reset token when code valid`() = runTest {
        coEvery { authService.verifyResetCode(any()) } returns VerifyResetCodeResponseDto(resetToken = "reset-token-uuid")

        val result = repository.verifyPasswordResetCode("john@example.com", "123456")

        assertTrue(result is Resource.Success)
        assertEquals("reset-token-uuid", (result as Resource.Success).data)
    }

    @Test
    fun `verifyPasswordResetCode maps error code 1009 to code field error`() = runTest {
        val exception = httpException(400)
        coEvery { authService.verifyResetCode(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1009, "Invalid code")

        val result = repository.verifyPasswordResetCode("john@example.com", "wrong") as Resource.Error

        assertEquals("Invalid code", result.fieldErrors?.get("code"))
    }

    @Test
    fun `resetPassword returns success when token valid`() = runTest {
        coEvery { authService.resetPassword(any()) } just Runs

        val result = repository.resetPassword("reset-token-uuid", "newpassword123")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `resetPassword returns error when http exception thrown`() = runTest {
        val exception = httpException(400)
        coEvery { authService.resetPassword(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1010, "Invalid reset token")

        val result = repository.resetPassword("bad-token", "newpassword123") as Resource.Error

        assertEquals("Invalid reset token", result.message)
        assertNull(result.fieldErrors)
    }
}
