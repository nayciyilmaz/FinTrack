package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.BudgetRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.BudgetMapper
import com.example.fintrack.data.remote.api.BudgetService
import com.example.fintrack.data.remote.dto.BudgetResponseDto
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.Budget
import io.mockk.coEvery
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

class BudgetRepositoryImplTest {

    private lateinit var budgetService: BudgetService
    private lateinit var budgetMapper: BudgetMapper
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: BudgetRepositoryImpl

    @Before
    fun setUp() {
        budgetService = mockk()
        budgetMapper = mockk()
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        repository = BudgetRepositoryImpl(budgetService, budgetMapper, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    @Test
    fun `getBudgets returns mapped list when api call succeeds`() = runTest {
        val dto = BudgetResponseDto(id = 1L, category = "FOOD", limitAmount = 500.0, createdAt = "2026-01-01")
        val budget = Budget(id = 1L, category = "FOOD", limitAmount = 500.0)
        coEvery { budgetService.getBudgets() } returns listOf(dto)
        every { budgetMapper.toBudget(dto) } returns budget

        val result = repository.getBudgets()

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
        assertEquals("FOOD", result.data?.get(0)?.category)
    }

    @Test
    fun `getBudgets returns error with parsed message when http exception thrown`() = runTest {
        val exception = httpException(401)
        coEvery { budgetService.getBudgets() } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1001, "User not found")

        val result = repository.getBudgets()

        assertTrue(result is Resource.Error)
        assertEquals("User not found", (result as Resource.Error).message)
    }

    @Test
    fun `getBudgets returns fallback message when unexpected exception has no message`() = runTest {
        coEvery { budgetService.getBudgets() } throws RuntimeException()

        val result = repository.getBudgets()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `saveBudgets returns mapped list when api call succeeds`() = runTest {
        val dto = BudgetResponseDto(id = 1L, category = "FOOD", limitAmount = 600.0, createdAt = "2026-01-01")
        val budget = Budget(id = 1L, category = "FOOD", limitAmount = 600.0)
        coEvery { budgetService.saveBudgets(any()) } returns listOf(dto)
        every { budgetMapper.toBudget(dto) } returns budget

        val result = repository.saveBudgets(listOf("FOOD" to 600.0))

        assertTrue(result is Resource.Success)
        assertEquals(600.0, (result as Resource.Success).data?.get(0)?.limitAmount)
    }

    @Test
    fun `saveBudgets returns error when http exception thrown`() = runTest {
        val exception = httpException(404)
        coEvery { budgetService.saveBudgets(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1001, "User not found")

        val result = repository.saveBudgets(listOf("FOOD" to 500.0))

        assertTrue(result is Resource.Error)
        assertEquals("User not found", (result as Resource.Error).message)
    }
}
