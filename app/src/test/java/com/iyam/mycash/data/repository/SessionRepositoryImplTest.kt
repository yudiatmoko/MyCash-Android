package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.recap.DataRecapResponse
import com.iyam.mycash.data.network.api.model.recap.DetailsDataRecap
import com.iyam.mycash.data.network.api.model.recap.RecapResponse
import com.iyam.mycash.data.network.api.model.recap.SessionDataRecap
import com.iyam.mycash.data.network.api.model.recap.SessionPaymentSummary
import com.iyam.mycash.data.network.api.model.recap.SessionUser
import com.iyam.mycash.data.network.api.model.recap.TopProductDataRecap
import com.iyam.mycash.data.network.api.model.session.DataSessionResponse
import com.iyam.mycash.data.network.api.model.session.RecapSessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionResponse
import com.iyam.mycash.data.network.api.model.session.toSession
import com.iyam.mycash.data.network.api.model.uploadimage.DataUploadImage
import com.iyam.mycash.data.network.api.model.uploadimage.UploadImageResponse
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Before
import org.junit.Test

class SessionRepositoryImplTest {

    @MockK
    lateinit var datasource: ApiDataSource

    private lateinit var repository: SessionRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = SessionRepositoryImpl(datasource)
    }

    @Test
    fun `add session result success`() {
        val mockResponse = SessionResponse(
            message = "message",
            status = "status",
            data = DataSessionResponse(
                checkInTime = "08:00",
                checkOutTime = "16:00",
                createdAt = "2025-03-16T10:00:00Z",
                date = "2025-03-16",
                id = "session-123",
                outletId = "outlet-789",
                shift = "Pagi",
                startingCash = 500000.0,
                totalRevenue = 1500000.0,
                updatedAt = "2025-03-16T18:00:00Z",
                userId = "user-456"
            )
        )
        val mockRequest = mockk<SessionRequest>(relaxed = true)
        coEvery { datasource.addSession(any()) } returns mockResponse
        runTest {
            repository.addSession(mockRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toSession()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.addSession(mockRequest) }
            }
        }
    }

    @Test
    fun `add session result loading`() {
        val mockResponse = SessionResponse(
            message = "message",
            status = "status",
            data = DataSessionResponse(
                checkInTime = "08:00",
                checkOutTime = "16:00",
                createdAt = "2025-03-16T10:00:00Z",
                date = "2025-03-16",
                id = "session-123",
                outletId = "outlet-789",
                shift = "Pagi",
                startingCash = 500000.0,
                totalRevenue = 1500000.0,
                updatedAt = "2025-03-16T18:00:00Z",
                userId = "user-456"
            )
        )
        val mockRequest = mockk<SessionRequest>(relaxed = true)
        coEvery { datasource.addSession(any()) } returns mockResponse
        runTest {
            repository.addSession(mockRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.addSession(mockRequest) }
            }
        }
    }

    @Test
    fun `add session result error`() {
        val mockRequest = mockk<SessionRequest>(relaxed = true)
        coEvery { datasource.addSession(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.addSession(mockRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.addSession(mockRequest) }
            }
        }
    }

    @Test
    fun `update session result success`() {
        val mockResponse = SessionResponse(
            message = "message",
            status = "status",
            data = DataSessionResponse(
                checkInTime = "08:00",
                checkOutTime = "16:00",
                createdAt = "2025-03-16T10:00:00Z",
                date = "2025-03-16",
                id = "session-123",
                outletId = "outlet-789",
                shift = "Pagi",
                startingCash = 500000.0,
                totalRevenue = 1500000.0,
                updatedAt = "2025-03-16T18:00:00Z",
                userId = "user-456"
            )
        )
        val mockRequest = mockk<RecapSessionRequest>(relaxed = true)
        val mockId = "123"
        coEvery { datasource.updateSession(any(), any()) } returns mockResponse
        runTest {
            repository.updateSession(mockId, mockRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toSession()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.updateSession(mockId, mockRequest) }
            }
        }
    }

    @Test
    fun `update session result error`() {
        val mockRequest = mockk<RecapSessionRequest>(relaxed = true)
        val mockId = "123"
        coEvery { datasource.updateSession(any(), any()) } throws ApiException(
            "Api Exception",
            500,
            null
        )
        runTest {
            repository.updateSession(mockId, mockRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.updateSession(mockId, mockRequest) }
            }
        }
    }

    @Test
    fun `recap by outlet result success`() {
        val mockResponse = RecapResponse(
            data = DataRecapResponse(
                details = DetailsDataRecap(
                    endDate = "2025-03-16",
                    paymentSummary = SessionPaymentSummary(
                        cash = 1000000,
                        qris = 500000
                    ),
                    startDate = "2025-03-10",
                    successfulTransactions = 150,
                    totalRevenue = 2250000,
                    totalSessions = 5,
                    totalTransactions = 160,
                    voidedTransactions = 10
                ),
                sessions = listOf(
                    SessionDataRecap(
                        checkInTime = "08:00",
                        checkOutTime = "16:00",
                        date = "2025-03-16",
                        outletId = "outlet-789",
                        paymentSummary = SessionPaymentSummary(
                            cash = 1000000,
                            qris = 500000
                        ),
                        sessionId = "session-123",
                        shift = "Pagi",
                        startingCash = 500000.0,
                        successfulTransactions = 30,
                        totalRevenue = 700000.0,
                        totalTransactions = 32,
                        user = SessionUser(
                            name = "Ilham Yudiatmoko"
                        ),
                        voidedTransactions = 2
                    )
                ),
                topProducts = listOf(
                    TopProductDataRecap(
                        name = "Ayam Goreng Crispy",
                        quantity = 50
                    ),
                    TopProductDataRecap(
                        name = "Minuman Soda",
                        quantity = 30
                    )
                )
            ),
            message = "Success",
            status = "200"
        )
        val mockId = "123"
        coEvery { datasource.recapByOutlet(any(), any(), any(), any()) } returns mockResponse
        runTest {
            repository.recapByOutlet(mockId, null, null, null).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(mockResponse, data.payload)
                coVerify { datasource.recapByOutlet(mockId, null, null, null) }
            }
        }
    }

    @Test
    fun `recap by outlet result error`() {
        val mockId = "123"
        coEvery {
            datasource.recapByOutlet(
                any(),
                any(),
                any(),
                any()
            )
        } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.recapByOutlet(mockId, null, null, null).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.recapByOutlet(mockId, null, null, null) }
            }
        }
    }

    @Test
    fun `upload session image result success`() {
        val mockResponse = UploadImageResponse(
            message = "message",
            status = "status",
            data = DataUploadImage(
                image = "2025-03-16",
                id = "session-123",
                sessionId = "outlet-789"
            )
        )
        val mockId = "123"
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery { datasource.uploadSessionImage(any(), any()) } returns mockResponse
        runTest {
            repository.uploadSessionImage(mockId, mockImage).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.image
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.uploadSessionImage(mockId, mockImage) }
            }
        }
    }

    @Test
    fun `upload session image result error`() {
        val mockId = "123"
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery { datasource.uploadSessionImage(any(), any()) } throws ApiException(
            "Api Exception",
            500,
            null
        )
        runTest {
            repository.uploadSessionImage(mockId, mockImage).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.uploadSessionImage(mockId, mockImage) }
            }
        }
    }
}
