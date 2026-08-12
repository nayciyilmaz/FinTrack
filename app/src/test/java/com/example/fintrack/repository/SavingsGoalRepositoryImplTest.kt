package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.SavingsGoalRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.SavingsGoalMapper
import com.example.fintrack.data.remote.api.SavingsGoalService
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.dto.SavingsGoalResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.SavingsGoal
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

class SavingsGoalRepositoryImplTest {

    private lateinit var savingsGoalService: SavingsGoalService
    private lateinit var savingsGoalMapper: SavingsGoalMapper
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: SavingsGoalRepositoryImpl

    @Before
    fun setUp() {
        savingsGoalService = mockk()
        savingsGoalMapper = mockk()
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        repository = SavingsGoalRepositoryImpl(savingsGoalService, savingsGoalMapper, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    private fun buildDto() = SavingsGoalResponseDto(
        id = 1L, name = "Vacation", category = "TRAVEL",
        targetAmount = 5000.0, currentAmount = 0.0, createdAt = "2026-01-01"
    )

    private fun buildGoal() = SavingsGoal(
        id = 1L, name = "Vacation", category = "TRAVEL",
        targetAmount = 5000.0, currentAmount = 0.0, createdAt = "2026-01-01"
    )

    @Test
    fun `getGoals returns mapped list when api call succeeds`() = runTest {
        val dto = buildDto()
        val goal = buildGoal()
        coEvery { savingsGoalService.getGoals() } returns listOf(dto)
        every { savingsGoalMapper.toSavingsGoal(dto) } returns goal

        val result = repository.getGoals()

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getGoals returns error when http exception thrown`() = runTest {
        val exception = httpException(401)
        coEvery { savingsGoalService.getGoals() } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.getGoals()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `addGoal returns success when api call succeeds`() = runTest {
        val dto = buildDto()
        val goal = buildGoal()
        coEvery { savingsGoalService.addGoal(any()) } returns dto
        every { savingsGoalMapper.toSavingsGoal(dto) } returns goal

        val result = repository.addGoal("Vacation", "TRAVEL", 5000.0)

        assertTrue(result is Resource.Success)
        assertEquals("Vacation", (result as Resource.Success).data?.name)
    }

    @Test
    fun `addGoal returns error when http exception thrown`() = runTest {
        val exception = httpException(400)
        coEvery { savingsGoalService.addGoal(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1001, "User not found")

        val result = repository.addGoal("Vacation", "TRAVEL", 5000.0)

        assertTrue(result is Resource.Error)
        assertEquals("User not found", (result as Resource.Error).message)
    }

    @Test
    fun `updateGoal returns success when api call succeeds`() = runTest {
        val dto = buildDto().copy(currentAmount = 200.0)
        val goal = buildGoal().copy(currentAmount = 200.0)
        coEvery { savingsGoalService.updateGoal(1L, any()) } returns dto
        every { savingsGoalMapper.toSavingsGoal(dto) } returns goal

        val result = repository.updateGoal(1L, 200.0, null)

        assertTrue(result is Resource.Success)
        assertEquals(200.0, (result as Resource.Success).data?.currentAmount)
    }

    @Test
    fun `updateGoal returns error when goal not found`() = runTest {
        val exception = httpException(404)
        coEvery { savingsGoalService.updateGoal(99L, any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1008, "Goal not found")

        val result = repository.updateGoal(99L, 200.0, null)

        assertTrue(result is Resource.Error)
        assertEquals("Goal not found", (result as Resource.Error).message)
    }

    @Test
    fun `deleteGoal returns success when api call succeeds`() = runTest {
        coEvery { savingsGoalService.deleteGoal(1L) } just Runs

        val result = repository.deleteGoal(1L)

        assertTrue(result is Resource.Success)
    }

    @Test
    fun `deleteGoal returns error when goal not found`() = runTest {
        val exception = httpException(404)
        coEvery { savingsGoalService.deleteGoal(99L) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1008, "Goal not found")

        val result = repository.deleteGoal(99L)

        assertTrue(result is Resource.Error)
        assertEquals("Goal not found", (result as Resource.Error).message)
    }
}
