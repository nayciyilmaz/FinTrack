package com.example.fintrack.interceptor

import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.remote.interceptor.TokenAuthenticator
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.json.Json
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TokenAuthenticatorTest {

    private lateinit var tokenManager: TokenManager
    private lateinit var authenticator: TokenAuthenticator

    @Before
    fun setUp() {
        tokenManager = mockk()
        authenticator = TokenAuthenticator(
            tokenManager = tokenManager,
            json = Json,
            refreshHttpClient = mockk(),
            refreshUrl = "http://localhost/api/auth/refresh"
        )
    }

    private fun buildResponse(requestToken: String?, priorResponse: Response? = null): Response {
        val requestBuilder = Request.Builder().url("http://localhost/api/transactions")
        requestToken?.let { requestBuilder.header("Authorization", "Bearer $it") }
        return Response.Builder()
            .request(requestBuilder.build())
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .priorResponse(priorResponse)
            .build()
    }

    @Test
    fun `authenticate returns null when already retried twice`() {
        val firstAttempt = buildResponse(requestToken = "old-token")
        val secondAttempt = buildResponse(requestToken = "old-token", priorResponse = firstAttempt)

        val result = authenticator.authenticate(null, secondAttempt)

        assertNull(result)
    }

    @Test
    fun `authenticate reuses token already refreshed by another request`() {
        val response = buildResponse(requestToken = "old-token")
        every { tokenManager.getToken() } returns flowOf("new-token")

        val result = authenticator.authenticate(null, response)

        assertEquals("Bearer new-token", result?.header("Authorization"))
    }

    @Test
    fun `authenticate clears session when no refresh token available`() {
        val response = buildResponse(requestToken = "old-token")
        every { tokenManager.getToken() } returns flowOf("old-token")
        every { tokenManager.getRefreshToken() } returns flowOf(null)
        coEvery { tokenManager.clearAll() } just Runs
        every { tokenManager.notifySessionExpired() } just Runs

        val result = authenticator.authenticate(null, response)

        assertNull(result)
        coVerify { tokenManager.clearAll() }
        verify { tokenManager.notifySessionExpired() }
    }
}
