package com.example.fintrack.data.remote.interceptor

import com.example.fintrack.data.local.TokenManager
import com.example.fintrack.data.remote.dto.RefreshRequestDto
import com.example.fintrack.data.remote.dto.TokenResponseDto
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val json: Json,
    @Named("tokenRefreshClient") private val refreshHttpClient: OkHttpClient,
    @Named("tokenRefreshUrl") private val refreshUrl: String
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        synchronized(lock) {
            val currentToken = runBlocking { tokenManager.getToken().firstOrNull() }
            val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")

            if (currentToken != null && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = runBlocking { tokenManager.getRefreshToken().firstOrNull() }

            if (refreshToken == null) {
                Timber.w("Token refresh skipped, no refresh token available")
                runBlocking { tokenManager.clearAll() }
                tokenManager.notifySessionExpired()
                return null
            }

            return try {
                val body = json.encodeToString(RefreshRequestDto.serializer(), RefreshRequestDto(refreshToken))
                    .toRequestBody("application/json".toMediaType())

                val refreshRequest = Request.Builder()
                    .url(refreshUrl)
                    .post(body)
                    .build()

                val refreshResponse = refreshHttpClient.newCall(refreshRequest).execute()

                if (refreshResponse.isSuccessful) {
                    val responseBody = refreshResponse.body?.string() ?: return null
                    val tokenResponse = json.decodeFromString<TokenResponseDto>(responseBody)
                    runBlocking {
                        tokenManager.saveToken(tokenResponse.token)
                        tokenManager.saveRefreshToken(tokenResponse.refreshToken)
                    }
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.token}")
                        .build()
                } else {
                    Timber.w("Token refresh request failed: status=%d", refreshResponse.code)
                    runBlocking { tokenManager.clearAll() }
                    tokenManager.notifySessionExpired()
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Token refresh failed")
                runBlocking { tokenManager.clearAll() }
                tokenManager.notifySessionExpired()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}
