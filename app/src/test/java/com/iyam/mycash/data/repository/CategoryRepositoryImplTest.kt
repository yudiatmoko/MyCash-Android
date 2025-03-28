package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.BaseResponse
import com.iyam.mycash.data.network.api.model.category.CategoriesResponse
import com.iyam.mycash.data.network.api.model.category.CategoryRequest
import com.iyam.mycash.data.network.api.model.category.CategoryResponse
import com.iyam.mycash.data.network.api.model.category.DataCategoryResponse
import com.iyam.mycash.data.network.api.model.category.toCategory
import com.iyam.mycash.data.network.api.model.category.toCategoryList
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
import org.junit.Before
import org.junit.Test

class CategoryRepositoryImplTest {

    @MockK
    lateinit var datasource: ApiDataSource

    private lateinit var repository: CategoryRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CategoryRepositoryImpl(datasource)
    }

    @Test
    fun `add category result success`() {
        val mockResponse = CategoryResponse(
            message = "message",
            status = "status",
            data = DataCategoryResponse(
                createdAt = "2024-03-16T10:00:00Z",
                id = "product-456",
                slug = "slug",
                name = "Produk Contoh",
                outletId = "outlet-789",
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockRequest = mockk<CategoryRequest>(relaxed = true)
        coEvery { datasource.addCategory(any()) } returns mockResponse
        runTest {
            repository.addCategory(mockRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toCategory()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.addCategory(mockRequest) }
            }
        }
    }

    @Test
    fun `add category result loading`() {
        val mockResponse = CategoryResponse(
            message = "message",
            status = "status",
            data = DataCategoryResponse(
                createdAt = "2024-03-16T10:00:00Z",
                id = "product-456",
                slug = "slug",
                name = "Produk Contoh",
                outletId = "outlet-789",
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockRequest = mockk<CategoryRequest>(relaxed = true)
        coEvery { datasource.addCategory(any()) } returns mockResponse
        runTest {
            repository.addCategory(mockRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.addCategory(mockRequest) }
            }
        }
    }

    @Test
    fun `add category result error`() {
        val mockRequest = mockk<CategoryRequest>(relaxed = true)
        coEvery { datasource.addCategory(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.addCategory(mockRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.addCategory(mockRequest) }
            }
        }
    }

    @Test
    fun `category by id result success`() {
        val mockResponse = CategoryResponse(
            message = "message",
            status = "status",
            data = DataCategoryResponse(
                createdAt = "2024-03-16T10:00:00Z",
                id = "product-456",
                slug = "slug",
                name = "Produk Contoh",
                outletId = "outlet-789",
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockId = "123"
        coEvery { datasource.categoryById(any()) } returns mockResponse
        runTest {
            repository.categoryById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toCategory()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.categoryById(mockId) }
            }
        }
    }

    @Test
    fun `category by id result loading`() {
        val mockResponse = CategoryResponse(
            message = "message",
            status = "status",
            data = DataCategoryResponse(
                createdAt = "2024-03-16T10:00:00Z",
                id = "product-456",
                slug = "slug",
                name = "Produk Contoh",
                outletId = "outlet-789",
                updatedAt = "2024-03-16T12:00:00Z"
            )
        )
        val mockId = "123"
        coEvery { datasource.categoryById(any()) } returns mockResponse
        runTest {
            repository.categoryById(mockId).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.categoryById(mockId) }
            }
        }
    }

    @Test
    fun `category by id result error`() {
        val mockId = "123"
        coEvery { datasource.categoryById(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.categoryById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.categoryById(mockId) }
            }
        }
    }

    @Test
    fun `categories by outlet result success`() {
        val mockResponse = CategoriesResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataCategoryResponse(
                    createdAt = "2024-03-16T10:00:00Z",
                    id = "product-456",
                    slug = "slug",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    updatedAt = "2024-03-16T12:00:00Z"
                ),
                DataCategoryResponse(
                    createdAt = "2024-03-16T10:00:00Z",
                    id = "product-456",
                    slug = "slug",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    updatedAt = "2024-03-16T12:00:00Z"
                )
            )
        )
        val mockId = "123"
        coEvery { datasource.categoriesByOutlet(any(), any()) } returns mockResponse
        runTest {
            repository.categoriesByOutlet(mockId, null).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toCategoryList()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.categoriesByOutlet(mockId, null) }
            }
        }
    }

    @Test
    fun `categories by outlet result loading`() {
        val mockResponse = CategoriesResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataCategoryResponse(
                    createdAt = "2024-03-16T10:00:00Z",
                    id = "product-456",
                    slug = "slug",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    updatedAt = "2024-03-16T12:00:00Z"
                ),
                DataCategoryResponse(
                    createdAt = "2024-03-16T10:00:00Z",
                    id = "product-456",
                    slug = "slug",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    updatedAt = "2024-03-16T12:00:00Z"
                )
            )
        )
        val mockId = "123"
        coEvery { datasource.categoriesByOutlet(any(), any()) } returns mockResponse
        runTest {
            repository.categoriesByOutlet(mockId, null).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.categoriesByOutlet(mockId, null) }
            }
        }
    }

    @Test
    fun `categories by outlet result error`() {
        val mockId = "123"
        coEvery { datasource.categoriesByOutlet(any(), any()) } throws ApiException(
            "Api Exception",
            500,
            null
        )
        runTest {
            repository.categoriesByOutlet(mockId, null).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.categoriesByOutlet(mockId, null) }
            }
        }
    }

    @Test
    fun `delete category result success`() {
        val mockResponse = BaseResponse(
            message = "message",
            status = "status"
        )
        val mockId = "123"
        coEvery { datasource.deleteCategory(any()) } returns mockResponse
        runTest {
            repository.deleteCategory(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.message
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.deleteCategory(mockId) }
            }
        }
    }

    @Test
    fun `delete category result loading`() {
        val mockResponse = BaseResponse(
            message = "message",
            status = "status"
        )
        val mockId = "123"
        coEvery { datasource.deleteCategory(any()) } returns mockResponse
        runTest {
            repository.deleteCategory(mockId).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.deleteCategory(mockId) }
            }
        }
    }

    @Test
    fun `delete category result error`() {
        val mockId = "123"
        coEvery { datasource.deleteCategory(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.deleteCategory(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.deleteCategory(mockId) }
            }
        }
    }
}
