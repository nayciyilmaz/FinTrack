package com.example.fintrack.data.remote.api

import com.example.fintrack.data.remote.dto.RecurringItemResponseDto
import com.example.fintrack.data.remote.dto.RecurringItemUpdateRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RecurringItemService {

    @GET("api/recurring-items")
    suspend fun getRecurringItems(): List<RecurringItemResponseDto>

    @PUT("api/recurring-items/{id}")
    suspend fun updateRecurringItem(
        @Path("id") id: Long,
        @Body request: RecurringItemUpdateRequestDto
    ): RecurringItemResponseDto

    @DELETE("api/recurring-items/{id}")
    suspend fun deleteRecurringItem(@Path("id") id: Long)
}
