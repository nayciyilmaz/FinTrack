package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.TransactionRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.TransactionMapper
import com.example.fintrack.data.remote.api.TransactionService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.TransactionResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.Transaction
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

class TransactionRepositoryImplTest {

    private lateinit var transactionService: TransactionService
    private lateinit var transactionMapper: TransactionMapper
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: TransactionRepositoryImpl

    @Before
    fun setUp() {
        transactionService = mockk()
        transactionMapper = mockk()
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        repository = TransactionRepositoryImpl(transactionService, transactionMapper, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    private fun buildDto() = TransactionResponseDto(
        id = 1L, type = "EXPENSE", category = "FOOD", amount = 50.0, note = null,
        date = "2026-01-15", time = "12:00:00", recurring = false, reminder = false, createdAt = "2026-01-15T12:00:00"
    )

    private fun buildTransaction() = Transaction(
        id = 1L, type = TransactionType.EXPENSE, category = "FOOD", amount = 50.0, note = null,
        date = "2026-01-15", time = "12:00:00", isRecurring = false, isReminder = false, createdAt = "2026-01-15T12:00:00"
    )

    @Test
    fun `addTransaction returns success when api call succeeds`() = runTest {
        val dto = buildDto()
        val transaction = buildTransaction()
        coEvery { transactionService.addTransaction(any()) } returns dto
        every { transactionMapper.toTransaction(dto) } returns transaction

        val result = repository.addTransaction(
            TransactionType.EXPENSE, "FOOD", 50.0, null, "2026-01-15", "12:00:00", false, false
        )

        assertTrue(result is Resource.Success)
        assertEquals("FOOD", (result as Resource.Success).data?.category)
    }

    @Test
    fun `addTransaction returns error when http exception thrown`() = runTest {
        val exception = httpException(404)
        coEvery { transactionService.addTransaction(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1001, "User not found")

        val result = repository.addTransaction(
            TransactionType.EXPENSE, "FOOD", 50.0, null, "2026-01-15", "12:00:00", false, false
        )

        assertTrue(result is Resource.Error)
        assertEquals("User not found", (result as Resource.Error).message)
    }

    @Test
    fun `getTransactions passes null type when type not provided`() = runTest {
        coEvery {
            transactionService.getTransactions(isNull(), "2026-01-01", "2026-01-31")
        } returns listOf(buildDto())
        every { transactionMapper.toTransaction(any()) } returns buildTransaction()

        val result = repository.getTransactions(null, "2026-01-01", "2026-01-31")

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getTransactions passes type name when type provided`() = runTest {
        coEvery {
            transactionService.getTransactions("EXPENSE", "2026-01-01", "2026-01-31")
        } returns listOf(buildDto())
        every { transactionMapper.toTransaction(any()) } returns buildTransaction()

        val result = repository.getTransactions(TransactionType.EXPENSE, "2026-01-01", "2026-01-31")

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `getTransactions returns error when http exception thrown`() = runTest {
        val exception = httpException(500)
        coEvery { transactionService.getTransactions(isNull(), any(), any()) } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.getTransactions(null, "2026-01-01", "2026-01-31")

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `updateTransaction returns success when api call succeeds`() = runTest {
        val dto = buildDto().copy(category = "RENT")
        val transaction = buildTransaction().copy(category = "RENT")
        coEvery { transactionService.updateTransaction(1L, any()) } returns dto
        every { transactionMapper.toTransaction(dto) } returns transaction

        val result = repository.updateTransaction(
            1L, TransactionType.EXPENSE, "RENT", 50.0, null, "2026-01-15", "12:00:00", false, false
        )

        assertTrue(result is Resource.Success)
        assertEquals("RENT", (result as Resource.Success).data?.category)
    }

    @Test
    fun `updateTransaction returns error when transaction not found`() = runTest {
        val exception = httpException(404)
        coEvery { transactionService.updateTransaction(99L, any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1006, "Transaction not found")

        val result = repository.updateTransaction(
            99L, TransactionType.EXPENSE, "FOOD", 50.0, null, "2026-01-15", "12:00:00", false, false
        )

        assertTrue(result is Resource.Error)
        assertEquals("Transaction not found", (result as Resource.Error).message)
    }

    @Test
    fun `deleteTransaction returns success when api call succeeds`() = runTest {
        coEvery { transactionService.deleteTransaction(1L) } just Runs

        val result = repository.deleteTransaction(1L)

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `deleteTransaction returns error when transaction not found`() = runTest {
        val exception = httpException(404)
        coEvery { transactionService.deleteTransaction(99L) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1006, "Transaction not found")

        val result = repository.deleteTransaction(99L)

        assertTrue(result is Resource.Error)
        assertEquals("Transaction not found", (result as Resource.Error).message)
    }

    @Test
    fun `getReminders returns mapped list when api call succeeds`() = runTest {
        val dto = buildDto().copy(reminder = true)
        val transaction = buildTransaction().copy(isReminder = true)
        coEvery { transactionService.getReminders() } returns listOf(dto)
        every { transactionMapper.toTransaction(dto) } returns transaction

        val result = repository.getReminders()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data?.get(0)?.isReminder)
    }

    @Test
    fun `getReminders returns error when http exception thrown`() = runTest {
        val exception = httpException(500)
        coEvery { transactionService.getReminders() } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.getReminders()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }
}
