package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.outlet.DataOutletResponse
import com.iyam.mycash.data.network.api.model.outlet.OutletRequest
import com.iyam.mycash.data.network.api.model.outlet.OutletResponse
import com.iyam.mycash.data.network.api.model.outlet.OutletsResponse
import com.iyam.mycash.data.network.api.model.outlet.toOutlet
import com.iyam.mycash.data.network.api.model.outlet.toOutletList
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
import okhttp3.RequestBody
import org.junit.Before
import org.junit.Test

class OutletRepositoryImplTest {

    @MockK
    lateinit var datasource: ApiDataSource

    private lateinit var repository: OutletRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = OutletRepositoryImpl(datasource)
    }

    @Test
    fun `add outlet result success`() {
        val mockResponse = OutletResponse(
            message = "message",
            status = "status",
            data = DataOutletResponse(
                address = "Jalan Sudirman No. 123",
                city = "Jakarta",
                createdAt = "2024-01-01T10:00:00Z",
                district = "Setiabudi",
                id = "outlet-123",
                image = "https://example.com/outlet.jpg",
                name = "Outlet Jakarta",
                phoneNumber = "+62123456789",
                province = "DKI Jakarta",
                type = "Restaurant",
                updatedAt = "2024-03-10T15:30:00Z",
                userId = "user-456"
            )
        )
        val mockAddOutletRequest = mockk<OutletRequest>(relaxed = true)
        coEvery { datasource.addOutlet(any()) } returns mockResponse
        runTest {
            repository.addOutlet(mockAddOutletRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toOutlet()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.addOutlet(mockAddOutletRequest) }
            }
        }
    }

    @Test
    fun `add outlet result loading`() {
        val mockResponse = OutletResponse(
            message = "message",
            status = "status",
            data = DataOutletResponse(
                address = "Jalan Sudirman No. 123",
                city = "Jakarta",
                createdAt = "2024-01-01T10:00:00Z",
                district = "Setiabudi",
                id = "outlet-123",
                image = "https://example.com/outlet.jpg",
                name = "Outlet Jakarta",
                phoneNumber = "+62123456789",
                province = "DKI Jakarta",
                type = "Restaurant",
                updatedAt = "2024-03-10T15:30:00Z",
                userId = "user-456"
            )
        )
        val mockAddOutletRequest = mockk<OutletRequest>(relaxed = true)
        coEvery { datasource.addOutlet(any()) } returns mockResponse
        runTest {
            repository.addOutlet(mockAddOutletRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.addOutlet(mockAddOutletRequest) }
            }
        }
    }

    @Test
    fun `add outlet result error`() {
        val mockAddOutletRequest = mockk<OutletRequest>(relaxed = true)
        coEvery { datasource.addOutlet(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.addOutlet(mockAddOutletRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.addOutlet(mockAddOutletRequest) }
            }
        }
    }

    @Test
    fun `update outlet result success`() {
        val mockResponse = OutletResponse(
            message = "message",
            status = "status",
            data = DataOutletResponse(
                address = "Jalan Sudirman No. 123",
                city = "Jakarta",
                createdAt = "2024-01-01T10:00:00Z",
                district = "Setiabudi",
                id = "outlet-123",
                image = "https://example.com/outlet.jpg",
                name = "Outlet Jakarta",
                phoneNumber = "+62123456789",
                province = "DKI Jakarta",
                type = "Restaurant",
                updatedAt = "2024-03-10T15:30:00Z",
                userId = "user-456"
            )
        )
        val mockId = "outlet-123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockType = mockk<RequestBody>(relaxed = true)
        val mockPhoneNumber = mockk<RequestBody>(relaxed = true)
        val mockAddress = mockk<RequestBody>(relaxed = true)
        val mockDistrict = mockk<RequestBody>(relaxed = true)
        val mockCity = mockk<RequestBody>(relaxed = true)
        val mockProvince = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.updateOutlet(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns mockResponse
        runTest {
            repository.updateOutlet(
                mockId,
                mockName,
                mockType,
                mockPhoneNumber,
                mockAddress,
                mockDistrict,
                mockCity,
                mockProvince,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toOutlet()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify {
                    datasource.updateOutlet(
                        mockId,
                        mockName,
                        mockType,
                        mockPhoneNumber,
                        mockAddress,
                        mockDistrict,
                        mockCity,
                        mockProvince,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `update outlet result loading`() {
        val mockResponse = OutletResponse(
            message = "message",
            status = "status",
            data = DataOutletResponse(
                address = "Jalan Sudirman No. 123",
                city = "Jakarta",
                createdAt = "2024-01-01T10:00:00Z",
                district = "Setiabudi",
                id = "outlet-123",
                image = "https://example.com/outlet.jpg",
                name = "Outlet Jakarta",
                phoneNumber = "+62123456789",
                province = "DKI Jakarta",
                type = "Restaurant",
                updatedAt = "2024-03-10T15:30:00Z",
                userId = "user-456"
            )
        )
        val mockId = "outlet-123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockType = mockk<RequestBody>(relaxed = true)
        val mockPhoneNumber = mockk<RequestBody>(relaxed = true)
        val mockAddress = mockk<RequestBody>(relaxed = true)
        val mockDistrict = mockk<RequestBody>(relaxed = true)
        val mockCity = mockk<RequestBody>(relaxed = true)
        val mockProvince = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.updateOutlet(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns mockResponse
        runTest {
            repository.updateOutlet(
                mockId,
                mockName,
                mockType,
                mockPhoneNumber,
                mockAddress,
                mockDistrict,
                mockCity,
                mockProvince,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify {
                    datasource.updateOutlet(
                        mockId,
                        mockName,
                        mockType,
                        mockPhoneNumber,
                        mockAddress,
                        mockDistrict,
                        mockCity,
                        mockProvince,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `update outlet result error`() {
        val mockId = "outlet-123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockType = mockk<RequestBody>(relaxed = true)
        val mockPhoneNumber = mockk<RequestBody>(relaxed = true)
        val mockAddress = mockk<RequestBody>(relaxed = true)
        val mockDistrict = mockk<RequestBody>(relaxed = true)
        val mockCity = mockk<RequestBody>(relaxed = true)
        val mockProvince = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.updateOutlet(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.updateOutlet(
                mockId,
                mockName,
                mockType,
                mockPhoneNumber,
                mockAddress,
                mockDistrict,
                mockCity,
                mockProvince,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify {
                    datasource.updateOutlet(
                        mockId,
                        mockName,
                        mockType,
                        mockPhoneNumber,
                        mockAddress,
                        mockDistrict,
                        mockCity,
                        mockProvince,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `outlet by id result success`() {
        val mockResponse = OutletResponse(
            message = "message",
            status = "status",
            data = DataOutletResponse(
                address = "Jalan Sudirman No. 123",
                city = "Jakarta",
                createdAt = "2024-01-01T10:00:00Z",
                district = "Setiabudi",
                id = "outlet-123",
                image = "https://example.com/outlet.jpg",
                name = "Outlet Jakarta",
                phoneNumber = "+62123456789",
                province = "DKI Jakarta",
                type = "Restaurant",
                updatedAt = "2024-03-10T15:30:00Z",
                userId = "user-456"
            )
        )
        val mockId = "123"
        coEvery { datasource.outletById(any()) } returns mockResponse
        runTest {
            repository.outletById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toOutlet()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.outletById(mockId) }
            }
        }
    }

    @Test
    fun `outlet by id result loading`() {
        val mockResponse = OutletResponse(
            message = "message",
            status = "status",
            data = DataOutletResponse(
                address = "Jalan Sudirman No. 123",
                city = "Jakarta",
                createdAt = "2024-01-01T10:00:00Z",
                district = "Setiabudi",
                id = "outlet-123",
                image = "https://example.com/outlet.jpg",
                name = "Outlet Jakarta",
                phoneNumber = "+62123456789",
                province = "DKI Jakarta",
                type = "Restaurant",
                updatedAt = "2024-03-10T15:30:00Z",
                userId = "user-456"
            )
        )
        val mockId = "123"
        coEvery { datasource.outletById(any()) } returns mockResponse
        runTest {
            repository.outletById(mockId).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.outletById(mockId) }
            }
        }
    }

    @Test
    fun `outlet by id result error`() {
        val mockId = "123"
        coEvery { datasource.outletById(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.outletById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.outletById(mockId) }
            }
        }
    }

    @Test
    fun `outlets by user result success`() {
        val mockResponse = OutletsResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataOutletResponse(
                    address = "Jalan Sudirman No. 123",
                    city = "Jakarta",
                    createdAt = "2024-01-01T10:00:00Z",
                    district = "Setiabudi",
                    id = "outlet-123",
                    image = "https://example.com/outlet.jpg",
                    name = "Outlet Jakarta",
                    phoneNumber = "+62123456789",
                    province = "DKI Jakarta",
                    type = "Restaurant",
                    updatedAt = "2024-03-10T15:30:00Z",
                    userId = "user-456"
                ),
                DataOutletResponse(
                    address = "Jalan Sudirman No. 123",
                    city = "Jakarta",
                    createdAt = "2024-01-01T10:00:00Z",
                    district = "Setiabudi",
                    id = "outlet-123",
                    image = "https://example.com/outlet.jpg",
                    name = "Outlet Jakarta",
                    phoneNumber = "+62123456789",
                    province = "DKI Jakarta",
                    type = "Restaurant",
                    updatedAt = "2024-03-10T15:30:00Z",
                    userId = "user-456"
                )
            )
        )
        val mockName = "123"
        coEvery { datasource.outletsByUser(any()) } returns mockResponse
        runTest {
            repository.outletsByUser(mockName).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toOutletList()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.outletsByUser(mockName) }
            }
        }
    }

    @Test
    fun `outlets by user result loading`() {
        val mockResponse = OutletsResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataOutletResponse(
                    address = "Jalan Sudirman No. 123",
                    city = "Jakarta",
                    createdAt = "2024-01-01T10:00:00Z",
                    district = "Setiabudi",
                    id = "outlet-123",
                    image = "https://example.com/outlet.jpg",
                    name = "Outlet Jakarta",
                    phoneNumber = "+62123456789",
                    province = "DKI Jakarta",
                    type = "Restaurant",
                    updatedAt = "2024-03-10T15:30:00Z",
                    userId = "user-456"
                ),
                DataOutletResponse(
                    address = "Jalan Sudirman No. 123",
                    city = "Jakarta",
                    createdAt = "2024-01-01T10:00:00Z",
                    district = "Setiabudi",
                    id = "outlet-123",
                    image = "https://example.com/outlet.jpg",
                    name = "Outlet Jakarta",
                    phoneNumber = "+62123456789",
                    province = "DKI Jakarta",
                    type = "Restaurant",
                    updatedAt = "2024-03-10T15:30:00Z",
                    userId = "user-456"
                )
            )
        )
        val mockName = "123"
        coEvery { datasource.outletsByUser(any()) } returns mockResponse
        runTest {
            repository.outletsByUser(mockName).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.outletsByUser(mockName) }
            }
        }
    }

    @Test
    fun `outlets by user result error`() {
        val mockName = "123"
        coEvery { datasource.outletsByUser(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.outletsByUser(mockName).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.outletsByUser(mockName) }
            }
        }
    }
}
