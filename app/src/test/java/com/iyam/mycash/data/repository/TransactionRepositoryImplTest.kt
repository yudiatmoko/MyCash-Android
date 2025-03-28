package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.BaseResponse
import com.iyam.mycash.data.network.api.model.transaction.DataTransactionResponse
import com.iyam.mycash.data.network.api.model.transaction.DetailTransaction
import com.iyam.mycash.data.network.api.model.transaction.OutletTransaction
import com.iyam.mycash.data.network.api.model.transaction.SessionTransaction
import com.iyam.mycash.data.network.api.model.transaction.TransactionResponse
import com.iyam.mycash.data.network.api.model.transaction.TransactionsResponse
import com.iyam.mycash.data.network.api.model.transaction.UserTransaction
import com.iyam.mycash.data.network.api.model.transaction.create.CreateTransactionResponse
import com.iyam.mycash.data.network.api.model.transaction.create.DataCreateTransactionResponse
import com.iyam.mycash.data.network.api.model.transaction.create.DetailTransactionRequest
import com.iyam.mycash.data.network.api.model.transaction.create.TransactionRequest
import com.iyam.mycash.data.network.api.model.transaction.toTransaction
import com.iyam.mycash.data.network.api.model.transaction.toTransactionList
import com.iyam.mycash.data.network.api.model.uploadimage.DataUploadImage
import com.iyam.mycash.data.network.api.model.uploadimage.UploadImageResponse
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class TransactionRepositoryImplTest {

    @MockK
    lateinit var datasource: ApiDataSource

    private lateinit var repository: TransactionRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = TransactionRepositoryImpl(datasource)
    }

    @Test
    fun `add transaction result success`() = runTest {
        val mockTransactionRequest = TransactionRequest(
            detail = listOf(
                DetailTransactionRequest(productId = "product-123", productQty = 2),
                DetailTransactionRequest(productId = "product-456", productQty = 1)
            ),
            note = "Pesanan dikirim segera",
            paymentMethod = "Credit Card",
            sessionId = "session-789",
            totalPayment = 75000.0
        )

        val mockResponse = CreateTransactionResponse(
            data = DataCreateTransactionResponse(transactionId = "trx-001"),
            message = "Transaction created successfully",
            status = "success"
        )

        coEvery { datasource.addTransaction(mockTransactionRequest) } returns mockResponse

        repository.addTransaction(mockTransactionRequest).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals("trx-001", result.payload.toString())
            coVerify { datasource.addTransaction(mockTransactionRequest) }
        }
    }

    @Test
    fun `add transaction result error`() = runTest {
        val mockTransactionRequest = TransactionRequest(
            detail = listOf(
                DetailTransactionRequest(productId = "product-123", productQty = 2),
                DetailTransactionRequest(productId = "product-456", productQty = 1)
            ),
            note = "Pesanan dikirim segera",
            paymentMethod = "Credit Card",
            sessionId = "session-789",
            totalPayment = 75000.0
        )

        coEvery { datasource.addTransaction(mockTransactionRequest) } throws ApiException(
            "Api Exception",
            500,
            null
        )

        repository.addTransaction(mockTransactionRequest).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { datasource.addTransaction(mockTransactionRequest) }
        }
    }

    @Test
    fun `transaction by id result success`() = runTest {
        val mockResponse = TransactionResponse(
            data = DataTransactionResponse(
                createdAt = "2025-03-16T10:00:00Z",
                date = "2025-03-16",
                details = listOf(
                    DetailTransaction(
                        id = "detail-001",
                        productId = "product-123",
                        productName = "Produk A",
                        productPrice = 25000.0,
                        productQty = 2,
                        totalPrice = 50000.0,
                        transactionId = "trx-001"
                    ),
                    DetailTransaction(
                        id = "detail-002",
                        productId = "product-456",
                        productName = "Produk B",
                        productPrice = 25000.0,
                        productQty = 1,
                        totalPrice = 25000.0,
                        transactionId = "trx-001"
                    )
                ),
                id = "trx-001",
                isVoided = false,
                note = "Pesanan dikirim segera",
                number = 1001,
                paymentMethod = "Credit Card",
                session = SessionTransaction(
                    date = "2025-03-16",
                    outlet = OutletTransaction(
                        address = "Jl. Contoh No.1",
                        city = "Bekasi",
                        district = "Bekasi Utara",
                        name = "Outlet ABC",
                        phoneNumber = "08123456789",
                        province = "Jawa Barat"
                    ),
                    shift = "Pagi",
                    user = UserTransaction(
                        name = "Kasir A"
                    )
                ),
                sessionId = "session-789",
                totalCharge = 75000.0,
                totalPayment = 75000.0,
                totalPrice = 75000.0,
                updatedAt = "2025-03-16T10:05:00Z"
            ),
            message = "Transaction created successfully",
            status = "success"
        )

        coEvery { datasource.transactionById(any()) } returns mockResponse

        repository.transactionById("id").map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(mockResponse.data.toTransaction(), result.payload)
            coVerify { datasource.transactionById(any()) }
        }
    }

    @Test
    fun `transaction by id result error`() = runTest {
        coEvery { datasource.transactionById(any()) } throws ApiException(
            "Api Exception",
            500,
            null
        )

        repository.transactionById("id").map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { datasource.transactionById(any()) }
        }
    }

    @Test
    fun `transaction list result success`() = runTest {
        val mockApiResponse = TransactionsResponse(
            message = "Transaction created successfully",
            status = "success",
            data = listOf(
                DataTransactionResponse(
                    id = "trx-001",
                    number = 1001,
                    date = "2025-03-16",
                    totalPrice = 75000.0,
                    totalPayment = 75000.0,
                    totalCharge = 75000.0,
                    paymentMethod = "Credit Card",
                    note = "Pesanan dikirim segera",
                    sessionId = "session-789",
                    isVoided = false,
                    createdAt = "2025-03-16T10:00:00Z",
                    updatedAt = "2025-03-16T10:05:00Z",
                    details = listOf(
                        DetailTransaction(
                            id = "detail-001",
                            productId = "product-123",
                            productName = "Produk A",
                            productPrice = 25000.0,
                            productQty = 2,
                            totalPrice = 50000.0,
                            transactionId = "trx-001"
                        ),
                        DetailTransaction(
                            id = "detail-002",
                            productId = "product-456",
                            productName = "Produk B",
                            productPrice = 25000.0,
                            productQty = 1,
                            totalPrice = 25000.0,
                            transactionId = "trx-001"
                        )
                    ),
                    session = SessionTransaction(
                        date = "2025-03-16",
                        outlet = OutletTransaction(
                            address = "Jl. Contoh No.1",
                            city = "Bekasi",
                            district = "Bekasi Utara",
                            name = "Outlet ABC",
                            phoneNumber = "08123456789",
                            province = "Jawa Barat"
                        ),
                        shift = "Pagi",
                        user = UserTransaction(
                            name = "Kasir A"
                        )
                    )
                )
            )
        )

        coEvery { datasource.transactionsBySession(any(), any(), any()) } returns mockApiResponse

        repository.transactionList("session-789", null, null).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(mockApiResponse.data.toTransactionList(), result.payload)
            coVerify { datasource.transactionsBySession("session-789", null, null) }
        }
    }

    @Test
    fun `transaction list result error`() = runTest {
        coEvery {
            datasource.transactionsBySession(
                any(),
                any(),
                any()
            )
        } throws ApiException("Api Exception", 500, null)

        repository.transactionList("session-789", null, null).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { datasource.transactionsBySession("session-789", null, null) }
        }
    }

    @Test
    fun `void transaction result success`() = runTest {
        val mockResponse = BaseResponse(
            status = "success",
            message = "Void Transaction Successfully"
        )
        coEvery { datasource.voidTransaction(any()) } returns mockResponse
        repository.voidTransaction("123").map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(mockResponse.message, result.payload)
            coVerify { datasource.voidTransaction(any()) }
        }
    }

    @Test
    fun `void transaction result error`() = runTest {
        coEvery { datasource.voidTransaction(any()) } throws ApiException(
            "Api Exception",
            500,
            null
        )
        repository.voidTransaction("123").map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { datasource.voidTransaction(any()) }
        }
    }

    @Test
    fun `upload transaction image result success`() = runTest {
        val mockResponse = UploadImageResponse(
            status = "success",
            message = "Upload Image Successfully",
            data = DataUploadImage(
                id = "id",
                image = "image",
                sessionId = "sesionId"
            )
        )
        val image = mockk<MultipartBody.Part>(relaxed = true)
        coEvery { datasource.uploadTransactionImage(any(), any()) } returns mockResponse
        repository.uploadTransactionImage("123", image).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(mockResponse.data.image, result.payload)
            coVerify { datasource.uploadTransactionImage(any(), any()) }
        }
    }

    @Test
    fun `upload transaction image result error`() = runTest {
        coEvery {
            datasource.uploadTransactionImage(
                any(),
                any()
            )
        } throws ApiException("Api Exception", 500, null)
        val image = mockk<MultipartBody.Part>(relaxed = true)
        repository.uploadTransactionImage("123", image).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { datasource.uploadTransactionImage(any(), any()) }
        }
    }
}
