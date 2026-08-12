package com.example.fintrack.repository

import android.content.Context
import com.example.fintrack.R
import com.example.fintrack.data.repository.AdvisorRepositoryImpl
import com.example.fintrack.core.util.Resource
import com.example.fintrack.data.mapper.AdvisorMapper
import com.example.fintrack.data.remote.api.AdvisorService
import com.example.fintrack.data.remote.dto.AdvisorInsightResponseDto
import com.example.fintrack.data.remote.dto.AdvisorSummaryResponseDto
import com.example.fintrack.data.remote.dto.ErrorResponseDto
import com.example.fintrack.data.remote.error.NetworkErrorParser
import com.example.fintrack.domain.model.AdvisorInsight
import com.example.fintrack.domain.model.AdvisorSummary
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

class AdvisorRepositoryImplTest {

    private lateinit var advisorService: AdvisorService
    private lateinit var advisorMapper: AdvisorMapper
    private lateinit var networkErrorParser: NetworkErrorParser
    private lateinit var context: Context
    private lateinit var repository: AdvisorRepositoryImpl

    @Before
    fun setUp() {
        advisorService = mockk()
        advisorMapper = mockk()
        networkErrorParser = mockk()
        context = mockk()
        every { context.getString(R.string.error_generic_fallback) } returns "generic error"
        repository = AdvisorRepositoryImpl(advisorService, advisorMapper, networkErrorParser, context)
    }

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }

    @Test
    fun `getSummary returns success when api call succeeds`() = runTest {
        val dto = AdvisorSummaryResponseDto(periodText = "1-31 Ocak", healthScore = 80, riskLevel = "Düşük")
        val summary = AdvisorSummary(periodText = "1-31 Ocak", healthScore = 80, riskLevel = "Düşük")
        coEvery { advisorService.getSummary() } returns dto
        every { advisorMapper.toAdvisorSummary(dto) } returns summary

        val result = repository.getSummary()

        assertTrue(result is Resource.Success)
        assertEquals(80, (result as Resource.Success).data?.healthScore)
    }

    @Test
    fun `getSummary returns error with parsed message when http exception thrown`() = runTest {
        val exception = httpException(500)
        coEvery { advisorService.getSummary() } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(9000, "Server error")

        val result = repository.getSummary()

        assertTrue(result is Resource.Error)
        assertEquals("Server error", (result as Resource.Error).message)
    }

    @Test
    fun `getSummary returns fallback message when unexpected exception has no message`() = runTest {
        coEvery { advisorService.getSummary() } throws RuntimeException()

        val result = repository.getSummary()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `getInsights returns mapped list when api call succeeds`() = runTest {
        val dto = AdvisorInsightResponseDto(id = 1L, categoryKey = "BUDGET", question = "q", answer = "a")
        val insight = AdvisorInsight(id = 1L, categoryKey = "BUDGET", question = "q", answer = "a")
        coEvery { advisorService.getInsights() } returns listOf(dto)
        every { advisorMapper.toAdvisorInsight(dto) } returns insight

        val result = repository.getInsights()

        assertTrue(result is Resource.Success)
        assertEquals(1, (result as Resource.Success).data?.size)
    }

    @Test
    fun `getInsights returns error when http exception thrown`() = runTest {
        val exception = httpException(401)
        coEvery { advisorService.getInsights() } throws exception
        every { networkErrorParser.parse(exception) } returns null

        val result = repository.getInsights()

        assertTrue(result is Resource.Error)
        assertEquals("generic error", (result as Resource.Error).message)
    }

    @Test
    fun `askQuestion returns success when api call succeeds`() = runTest {
        val dto = AdvisorInsightResponseDto(id = 2L, categoryKey = "BUDGET", question = "q2", answer = "a2")
        val insight = AdvisorInsight(id = 2L, categoryKey = "BUDGET", question = "q2", answer = "a2")
        coEvery { advisorService.askQuestion(any()) } returns dto
        every { advisorMapper.toAdvisorInsight(dto) } returns insight

        val result = repository.askQuestion("BUDGET", "q2")

        assertTrue(result is Resource.Success)
        assertEquals("a2", (result as Resource.Success).data?.answer)
    }

    @Test
    fun `askQuestion returns error when rate limited`() = runTest {
        val exception = httpException(429)
        coEvery { advisorService.askQuestion(any()) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1013, "Rate limit exceeded")

        val result = repository.askQuestion("BUDGET", "q")

        assertTrue(result is Resource.Error)
        assertEquals("Rate limit exceeded", (result as Resource.Error).message)
    }

    @Test
    fun `refreshInsight returns success when api call succeeds`() = runTest {
        val dto = AdvisorInsightResponseDto(id = 1L, categoryKey = "BUDGET", question = "q", answer = "updated")
        val insight = AdvisorInsight(id = 1L, categoryKey = "BUDGET", question = "q", answer = "updated")
        coEvery { advisorService.refreshInsight(1L) } returns dto
        every { advisorMapper.toAdvisorInsight(dto) } returns insight

        val result = repository.refreshInsight(1L)

        assertTrue(result is Resource.Success)
        assertEquals("updated", (result as Resource.Success).data?.answer)
    }

    @Test
    fun `refreshInsight returns error when insight not found`() = runTest {
        val exception = httpException(404)
        coEvery { advisorService.refreshInsight(99L) } throws exception
        every { networkErrorParser.parse(exception) } returns ErrorResponseDto(1011, "Insight not found")

        val result = repository.refreshInsight(99L)

        assertTrue(result is Resource.Error)
        assertEquals("Insight not found", (result as Resource.Error).message)
    }
}
