package com.example.fintrack.data.remote.error

import com.example.fintrack.data.remote.dto.ErrorResponseDto
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkErrorParser @Inject constructor(
    private val json: Json
) {
    fun parse(exception: HttpException): ErrorResponseDto? {
        return exception.response()?.errorBody()?.string()?.let {
            runCatching { json.decodeFromString<ErrorResponseDto>(it) }.getOrNull()
        }
    }
}