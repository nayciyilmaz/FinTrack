package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.RecurringItemRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.RecurringItemMapper
import com.example.fintrack.data.remote.api.RecurringItemService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.RecurringItemResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.RecurringItem
import com.example.fintrack.domain.model.TransactionType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
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

class RecurringItemRepositoryImplTest {

    private lateinit var recurringItemService: RecurringItemService
    private lateinit var recurringItemMapper: RecurringItemMapper
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: RecurringItemRepositoryImpl

    @Before
    fun setUp() {
        recurringItemService = mockk()
        recurringItemMapper = mockk()
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        repository = RecurringItemRepositoryImpl(recurringItemService, recurringItemMapper, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    @Test
    fun `getRecurringItems returns mapped list when api call succeeds`() = runTest {
        val dto = RecurringItemResponseDto(id = 1L, type = "INCOME", category = "SALARY", amount = 2000.0, dayOfMonth = 5)
        val item = RecurringItem(id = 1L, type = TransactionType.INCOME, category = "SALARY", amount = 2000.0, dayOfMonth = 5)
        coEvery { recurringItemService.getRecurringItems() } returns listOf(dto)
        every { recurringItemMapper.toRecurringItem(dto) } returns item

        val result = repository.getRecurringItems()

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getRecurringItems returns error when http exception thrown`() = runTest {
        val exception = httpException(500)
        coEvery { recurringItemService.getRecurringItems() } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.getRecurringItems()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `updateRecurringItem returns success when api call succeeds`() = runTest {
        val dto = RecurringItemResponseDto(id = 1L, type = "INCOME", category = "SALARY", amount = 2500.0, dayOfMonth = 10)
        val item = RecurringItem(id = 1L, type = TransactionType.INCOME, category = "SALARY", amount = 2500.0, dayOfMonth = 10)
        coEvery { recurringItemService.updateRecurringItem(1L, any()) } returns dto
        every { recurringItemMapper.toRecurringItem(dto) } returns item

        val result = repository.updateRecurringItem(1L, 2500.0, 10)

        assertTrue(result is Resource.Success)
        assertEquals(10, (result as Resource.Success).data?.dayOfMonth)
    }

    @Test
    fun `updateRecurringItem returns error when item not found`() = runTest {
        val exception = httpException(404)
        coEvery { recurringItemService.updateRecurringItem(99L, any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1014, "Recurring item not found")

        val result = repository.updateRecurringItem(99L, 2500.0, 10)

        assertTrue(result is Resource.Error)
        assertEquals("Recurring item not found", (result as Resource.Error).message)
    }

    @Test
    fun `deleteRecurringItem returns success when api call succeeds`() = runTest {
        coEvery { recurringItemService.deleteRecurringItem(1L) } just Runs

        val result = repository.deleteRecurringItem(1L)

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `deleteRecurringItem returns error when item not found`() = runTest {
        val exception = httpException(404)
        coEvery { recurringItemService.deleteRecurringItem(99L) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1014, "Recurring item not found")

        val result = repository.deleteRecurringItem(99L)

        assertTrue(result is Resource.Error)
        assertEquals("Recurring item not found", (result as Resource.Error).message)
    }
}
