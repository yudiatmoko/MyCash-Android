package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.BaseResponse
import com.iyam.mycash.data.network.api.model.product.DataProductResponse
import com.iyam.mycash.data.network.api.model.product.ProductResponse
import com.iyam.mycash.data.network.api.model.product.ProductsResponse
import com.iyam.mycash.data.network.api.model.product.toProduct
import com.iyam.mycash.data.network.api.model.product.toProductList
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

class ProductRepositoryImplTest {

    @MockK
    lateinit var datasource: ApiDataSource

    private lateinit var repository: ProductRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = ProductRepositoryImpl(datasource)
    }

    @Test
    fun `add product result success`() {
        val mockResponse = ProductResponse(
            message = "message",
            status = "status",
            data = DataProductResponse(
                categoryId = "category-123",
                createdAt = "2024-03-16T10:00:00Z",
                description = "Produk berkualitas tinggi",
                id = "product-456",
                image = "https://example.com/product-image.jpg",
                name = "Produk Contoh",
                outletId = "outlet-789",
                price = 15000.0f,
                status = true,
                stock = 100,
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockDescription = mockk<RequestBody>(relaxed = true)
        val mockPrice = mockk<RequestBody>(relaxed = true)
        val mockStatus = mockk<RequestBody>(relaxed = true)
        val mockStock = mockk<RequestBody>(relaxed = true)
        val mockCategoryId = mockk<RequestBody>(relaxed = true)
        val mockOutletId = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.addProduct(
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
            repository.addProduct(
                mockName,
                mockDescription,
                mockPrice,
                mockStatus,
                mockStock,
                mockCategoryId,
                mockOutletId,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toProduct()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify {
                    datasource.addProduct(
                        mockName,
                        mockDescription,
                        mockPrice,
                        mockStatus,
                        mockStock,
                        mockCategoryId,
                        mockOutletId,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `add product result loading`() {
        val mockResponse = ProductResponse(
            message = "message",
            status = "status",
            data = DataProductResponse(
                categoryId = "category-123",
                createdAt = "2024-03-16T10:00:00Z",
                description = "Produk berkualitas tinggi",
                id = "product-456",
                image = "https://example.com/product-image.jpg",
                name = "Produk Contoh",
                outletId = "outlet-789",
                price = 15000.0f,
                status = true,
                stock = 100,
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockDescription = mockk<RequestBody>(relaxed = true)
        val mockPrice = mockk<RequestBody>(relaxed = true)
        val mockStatus = mockk<RequestBody>(relaxed = true)
        val mockStock = mockk<RequestBody>(relaxed = true)
        val mockCategoryId = mockk<RequestBody>(relaxed = true)
        val mockOutletId = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.addProduct(
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
            repository.addProduct(
                mockName,
                mockDescription,
                mockPrice,
                mockStatus,
                mockStock,
                mockCategoryId,
                mockOutletId,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify {
                    datasource.addProduct(
                        mockName,
                        mockDescription,
                        mockPrice,
                        mockStatus,
                        mockStock,
                        mockCategoryId,
                        mockOutletId,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `add product result error`() {
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockDescription = mockk<RequestBody>(relaxed = true)
        val mockPrice = mockk<RequestBody>(relaxed = true)
        val mockStatus = mockk<RequestBody>(relaxed = true)
        val mockStock = mockk<RequestBody>(relaxed = true)
        val mockCategoryId = mockk<RequestBody>(relaxed = true)
        val mockOutletId = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.addProduct(
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
            repository.addProduct(
                mockName,
                mockDescription,
                mockPrice,
                mockStatus,
                mockStock,
                mockCategoryId,
                mockOutletId,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify {
                    datasource.addProduct(
                        mockName,
                        mockDescription,
                        mockPrice,
                        mockStatus,
                        mockStock,
                        mockCategoryId,
                        mockOutletId,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `update product result success`() {
        val mockResponse = ProductResponse(
            message = "message",
            status = "status",
            data = DataProductResponse(
                categoryId = "category-123",
                createdAt = "2024-03-16T10:00:00Z",
                description = "Produk berkualitas tinggi",
                id = "product-456",
                image = "https://example.com/product-image.jpg",
                name = "Produk Contoh",
                outletId = "outlet-789",
                price = 15000.0f,
                status = true,
                stock = 100,
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockId = "123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockDescription = mockk<RequestBody>(relaxed = true)
        val mockPrice = mockk<RequestBody>(relaxed = true)
        val mockStatus = mockk<RequestBody>(relaxed = true)
        val mockStock = mockk<RequestBody>(relaxed = true)
        val mockCategoryId = mockk<RequestBody>(relaxed = true)
        val mockOutletId = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.updateProduct(
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
            repository.updateProduct(
                mockId,
                mockName,
                mockDescription,
                mockPrice,
                mockStatus,
                mockStock,
                mockCategoryId,
                mockOutletId,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toProduct()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify {
                    datasource.updateProduct(
                        mockId,
                        mockName,
                        mockDescription,
                        mockPrice,
                        mockStatus,
                        mockStock,
                        mockCategoryId,
                        mockOutletId,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `update product result loading`() {
        val mockResponse = ProductResponse(
            message = "message",
            status = "status",
            data = DataProductResponse(
                categoryId = "category-123",
                createdAt = "2024-03-16T10:00:00Z",
                description = "Produk berkualitas tinggi",
                id = "product-456",
                image = "https://example.com/product-image.jpg",
                name = "Produk Contoh",
                outletId = "outlet-789",
                price = 15000.0f,
                status = true,
                stock = 100,
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockId = "123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockDescription = mockk<RequestBody>(relaxed = true)
        val mockPrice = mockk<RequestBody>(relaxed = true)
        val mockStatus = mockk<RequestBody>(relaxed = true)
        val mockStock = mockk<RequestBody>(relaxed = true)
        val mockCategoryId = mockk<RequestBody>(relaxed = true)
        val mockOutletId = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.updateProduct(
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
            repository.updateProduct(
                mockId,
                mockName,
                mockDescription,
                mockPrice,
                mockStatus,
                mockStock,
                mockCategoryId,
                mockOutletId,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify {
                    datasource.updateProduct(
                        mockId,
                        mockName,
                        mockDescription,
                        mockPrice,
                        mockStatus,
                        mockStock,
                        mockCategoryId,
                        mockOutletId,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `update product result error`() {
        val mockId = "123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockDescription = mockk<RequestBody>(relaxed = true)
        val mockPrice = mockk<RequestBody>(relaxed = true)
        val mockStatus = mockk<RequestBody>(relaxed = true)
        val mockStock = mockk<RequestBody>(relaxed = true)
        val mockCategoryId = mockk<RequestBody>(relaxed = true)
        val mockOutletId = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.updateProduct(
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
            repository.updateProduct(
                mockId,
                mockName,
                mockDescription,
                mockPrice,
                mockStatus,
                mockStock,
                mockCategoryId,
                mockOutletId,
                mockImage
            ).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify {
                    datasource.updateProduct(
                        mockId,
                        mockName,
                        mockDescription,
                        mockPrice,
                        mockStatus,
                        mockStock,
                        mockCategoryId,
                        mockOutletId,
                        mockImage
                    )
                }
            }
        }
    }

    @Test
    fun `product by id result success`() {
        val mockResponse = ProductResponse(
            message = "message",
            status = "status",
            data = DataProductResponse(
                categoryId = "category-123",
                createdAt = "2024-03-16T10:00:00Z",
                description = "Produk berkualitas tinggi",
                id = "product-456",
                image = "https://example.com/product-image.jpg",
                name = "Produk Contoh",
                outletId = "outlet-789",
                price = 15000.0f,
                status = true,
                stock = 100,
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockId = "123"
        coEvery { datasource.productById(any()) } returns mockResponse
        runTest {
            repository.productById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toProduct()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.productById(mockId) }
            }
        }
    }

    @Test
    fun `product by id result loading`() {
        val mockResponse = ProductResponse(
            message = "message",
            status = "status",
            data = DataProductResponse(
                categoryId = "category-123",
                createdAt = "2024-03-16T10:00:00Z",
                description = "Produk berkualitas tinggi",
                id = "product-456",
                image = "https://example.com/product-image.jpg",
                name = "Produk Contoh",
                outletId = "outlet-789",
                price = 15000.0f,
                status = true,
                stock = 100,
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockId = "123"
        coEvery { datasource.productById(any()) } returns mockResponse
        runTest {
            repository.productById(mockId).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.productById(mockId) }
            }
        }
    }

    @Test
    fun `product by id result error`() {
        val mockId = "123"
        coEvery { datasource.productById(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.productById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.productById(mockId) }
            }
        }
    }

    @Test
    fun `products by outlet result success`() {
        val mockResponse = ProductsResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataProductResponse(
                    categoryId = "category-123",
                    createdAt = "2024-03-16T10:00:00Z",
                    description = "Produk berkualitas tinggi",
                    id = "product-456",
                    image = "https://example.com/product-image.jpg",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    price = 15000.0f,
                    status = true,
                    stock = 100,
                    updatedAt = "2024-03-16T12:00:00Z"
                ),
                DataProductResponse(
                    categoryId = "category-123",
                    createdAt = "2024-03-16T10:00:00Z",
                    description = "Produk berkualitas tinggi",
                    id = "product-456",
                    image = "https://example.com/product-image.jpg",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    price = 15000.0f,
                    status = true,
                    stock = 100,
                    updatedAt = "2024-03-16T12:00:00Z"
                )
            )
        )
        val mockId = "123"
        coEvery { datasource.productsByOutlet(any()) } returns mockResponse
        runTest {
            repository.productsByOutlet(mockId, null, null, null).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toProductList()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.productsByOutlet(mockId) }
            }
        }
    }

    @Test
    fun `products by outlet result loading`() {
        val mockResponse = ProductsResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataProductResponse(
                    categoryId = "category-123",
                    createdAt = "2024-03-16T10:00:00Z",
                    description = "Produk berkualitas tinggi",
                    id = "product-456",
                    image = "https://example.com/product-image.jpg",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    price = 15000.0f,
                    status = true,
                    stock = 100,
                    updatedAt = "2024-03-16T12:00:00Z"
                ),
                DataProductResponse(
                    categoryId = "category-123",
                    createdAt = "2024-03-16T10:00:00Z",
                    description = "Produk berkualitas tinggi",
                    id = "product-456",
                    image = "https://example.com/product-image.jpg",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    price = 15000.0f,
                    status = true,
                    stock = 100,
                    updatedAt = "2024-03-16T12:00:00Z"
                )
            )
        )
        val mockId = "123"
        coEvery { datasource.productsByOutlet(any()) } returns mockResponse
        runTest {
            repository.productsByOutlet(mockId, null, null, null).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.productsByOutlet(mockId) }
            }
        }
    }

    @Test
    fun `products by outlet result error`() {
        val mockId = "123"
        coEvery { datasource.productsByOutlet(any()) } throws ApiException(
            "Api Exception",
            500,
            null
        )
        runTest {
            repository.productsByOutlet(mockId, null, null, null).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.productsByOutlet(mockId) }
            }
        }
    }

    @Test
    fun `delete product result success`() {
        val mockResponse = BaseResponse(
            message = "message",
            status = "status"
        )
        val mockId = "123"
        coEvery { datasource.deleteProduct(any()) } returns mockResponse
        runTest {
            repository.deleteProduct(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.message
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.deleteProduct(mockId) }
            }
        }
    }

    @Test
    fun `delete product result loading`() {
        val mockResponse = BaseResponse(
            message = "message",
            status = "status"
        )
        val mockId = "123"
        coEvery { datasource.deleteProduct(any()) } returns mockResponse
        runTest {
            repository.deleteProduct(mockId).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.deleteProduct(mockId) }
            }
        }
    }

    @Test
    fun `delete product result error`() {
        val mockId = "123"
        coEvery { datasource.deleteProduct(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.deleteProduct(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.deleteProduct(mockId) }
            }
        }
    }
}
