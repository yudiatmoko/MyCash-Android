package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.local.db.datasource.DatabaseDataSource
import com.iyam.mycash.data.local.db.entity.CartEntity
import com.iyam.mycash.data.local.db.mapper.toCartEntity
import com.iyam.mycash.data.local.db.mapper.toCartList
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.product.DataProductResponse
import com.iyam.mycash.data.network.api.model.product.ProductsResponse
import com.iyam.mycash.model.Cart
import com.iyam.mycash.model.Product
import com.iyam.mycash.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class CartRepositoryImplTest {

    @MockK
    lateinit var databaseDataSource: DatabaseDataSource

    @MockK
    lateinit var apiDataSource: ApiDataSource

    private lateinit var repository: CartRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = CartRepositoryImpl(databaseDataSource, apiDataSource)
    }

    @Test
    fun `get cart list result success`() {
        val mockResponse = listOf(
            CartEntity(
                id = 1,
                productId = "product-456",
                productName = "Produk Contoh",
                productPrice = 15000.0,
                productImg = "https://example.com/product-image.jpg",
                productStock = 100,
                productQuantity = 2
            ),
            CartEntity(
                id = 2,
                productId = "product-789",
                productName = "Produk Contoh 2",
                productPrice = 20000.0,
                productImg = "https://example.com/product-image2.jpg",
                productStock = 50,
                productQuantity = 1
            )
        )
        coEvery { databaseDataSource.getAllCarts() } returns flowOf(mockResponse)
        runTest {
            repository.getCartList().map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.toCartList()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload?.first)
                coVerify { databaseDataSource.getAllCarts() }
            }
        }
    }

    @Test
    fun `get cart list result loading`() {
        val mockResponse = listOf(
            CartEntity(
                id = 1,
                productId = "product-456",
                productName = "Produk Contoh",
                productPrice = 15000.0,
                productImg = "https://example.com/product-image.jpg",
                productStock = 100,
                productQuantity = 2
            ),
            CartEntity(
                id = 2,
                productId = "product-789",
                productName = "Produk Contoh 2",
                productPrice = 20000.0,
                productImg = "https://example.com/product-image2.jpg",
                productStock = 50,
                productQuantity = 1
            )
        )
        coEvery { databaseDataSource.getAllCarts() } returns flowOf(mockResponse)
        runTest {
            repository.getCartList().map {
                delay(100)
                it
            }.test {
                delay(600)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { databaseDataSource.getAllCarts() }
            }
        }
    }

    @Test
    fun `getProductWithUpdatedStock should return success`() = runTest {
        val mockProductsResponse = ProductsResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataProductResponse(
                    categoryId = "category-123",
                    createdAt = "2024-03-16T10:00:00Z",
                    description = "Produk berkualitas tinggi",
                    id = "product-789",
                    image = "https://example.com/product-image.jpg",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    price = 15000.0f,
                    status = true,
                    stock = 50,
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
        coEvery {
            apiDataSource.productsByOutlet(
                "outlet_001",
                null,
                null,
                null
            )
        } returns mockProductsResponse
        val mockCartResponse = listOf(
            CartEntity(
                id = 1,
                productId = "product-456",
                productName = "Produk Contoh",
                productPrice = 15000.0,
                productImg = "https://example.com/product-image.jpg",
                productStock = 100,
                productQuantity = 2
            ),
            CartEntity(
                id = 2,
                productId = "product-789",
                productName = "Produk Contoh 2",
                productPrice = 20000.0,
                productImg = "https://example.com/product-image2.jpg",
                productStock = 50,
                productQuantity = 1
            )
        )
        coEvery { databaseDataSource.getAllCarts() } returns flowOf(mockCartResponse)
        repository.getProductWithUpdatedStock("outlet_001", null, null, null).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(49, result.payload?.get(0)?.stock)
            assertEquals(98, result.payload?.get(1)?.stock)
            coVerify { apiDataSource.productsByOutlet("outlet_001", null, null, null) }
            coVerify { databaseDataSource.getAllCarts() }
        }
    }

    @Test
    fun `getProductWithUpdatedStock should return loading`() = runTest {
        val mockProductsResponse = ProductsResponse(
            message = "message",
            status = "status",
            data = listOf(
                DataProductResponse(
                    categoryId = "category-123",
                    createdAt = "2024-03-16T10:00:00Z",
                    description = "Produk berkualitas tinggi",
                    id = "product-789",
                    image = "https://example.com/product-image.jpg",
                    name = "Produk Contoh",
                    outletId = "outlet-789",
                    price = 15000.0f,
                    status = true,
                    stock = 50,
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
        coEvery {
            apiDataSource.productsByOutlet(
                "outlet_001",
                null,
                null,
                null
            )
        } returns mockProductsResponse
        val mockCartResponse = listOf(
            CartEntity(
                id = 1,
                productId = "product-456",
                productName = "Produk Contoh",
                productPrice = 15000.0,
                productImg = "https://example.com/product-image.jpg",
                productStock = 100,
                productQuantity = 2
            ),
            CartEntity(
                id = 2,
                productId = "product-789",
                productName = "Produk Contoh 2",
                productPrice = 20000.0,
                productImg = "https://example.com/product-image2.jpg",
                productStock = 50,
                productQuantity = 1
            )
        )
        coEvery { databaseDataSource.getAllCarts() } returns flowOf(mockCartResponse)
        repository.getProductWithUpdatedStock("outlet_001", null, null, null).map {
            delay(100)
            it
        }.test {
            delay(800)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Loading)
            coVerify { apiDataSource.productsByOutlet("outlet_001", null, null, null) }
            coVerify { databaseDataSource.getAllCarts() }
        }
    }

    @Test
    fun `getProductWithUpdatedStock should return error`() = runTest {
        coEvery {
            apiDataSource.productsByOutlet(
                any(),
                any(),
                any(),
                any()
            )
        } throws Exception("Network Error")
        coEvery { databaseDataSource.getAllCarts() } returns flowOf(emptyList())

        repository.getProductWithUpdatedStock("outlet_001", null, null, null).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { apiDataSource.productsByOutlet(any(), any(), any(), any()) }
        }
    }

    @Test
    fun `create cart success when product is new`() = runTest {
        val mockProduct = Product(
            id = "product-456",
            name = "Produk Contoh",
            description = "Produk berkualitas tinggi",
            price = 15000.0f,
            status = true,
            stock = 10,
            categoryId = "category-123",
            outletId = "outlet-789",
            image = "https://example.com/product-image.jpg"
        )
        val mockInsertResponse = 1L
        coEvery { databaseDataSource.getCartByProductId("product-456") } returns flowOf(null)
        coEvery { databaseDataSource.insertCart(any()) } returns mockInsertResponse

        repository.createCart(mockProduct, 2).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.insertCart(any()) }
        }
    }

    @Test
    fun `create cart success when product already exists in cart`() = runTest {
        val mockProduct = Product(
            id = "product-456",
            name = "Produk Contoh",
            price = 15000.0f,
            stock = 10,
            status = true,
            categoryId = "category-123",
            outletId = "outlet-789",
            image = "https://example.com/product-image.jpg",
            description = "Produk berkualitas tinggi"
        )
        val existingCart = CartEntity(
            productId = "product-456",
            productName = "Produk Contoh",
            productPrice = 15000.0,
            productImg = "https://example.com/product-image.jpg",
            productStock = 10,
            productQuantity = 3
        )
        val mockUpdateResponse = 1
        coEvery { databaseDataSource.getCartByProductId("product-456") } returns flowOf(existingCart)
        coEvery { databaseDataSource.updateCart(any()) } returns mockUpdateResponse

        repository.createCart(mockProduct, 2).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.updateCart(any()) }
        }
    }

    @Test
    fun `create cart should return error when stock is not enough`() = runTest {
        val mockProduct = Product(
            id = "product-456",
            name = "Produk Contoh",
            price = 15000.0f,
            stock = 1,
            status = true,
            categoryId = "category-123",
            outletId = "outlet-789",
            image = "https://example.com/product-image.jpg",
            description = "Produk berkualitas tinggi"
        )

        coEvery { databaseDataSource.getCartByProductId("product-456") } returns flowOf(null)

        repository.createCart(mockProduct, 5).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
        }
    }

    @Test
    fun `create cart should return error when product ID is null`() = runTest {
        val mockProduct = Product(
            id = null,
            name = "Produk Contoh",
            price = 15000.0f,
            stock = 10,
            status = true,
            categoryId = "category-123",
            outletId = "outlet-789",
            image = "https://example.com/product-image.jpg",
            description = "Produk berkualitas tinggi"
        )

        repository.createCart(mockProduct, 2).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
        }
    }

    @Test
    fun `decrease cart success - item decreased`() = runTest {
        val mockCart = Cart(
            productId = "product-123",
            productName = "Produk Contoh",
            productPrice = 15000.0,
            productQuantity = 2,
            productImg = "https://example.com/product.jpg",
            productStock = 10
        )

        val modifiedCart = mockCart.copy(productQuantity = 1)

        coEvery { databaseDataSource.updateCart(modifiedCart.toCartEntity()) } returns 1

        repository.decreaseCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.updateCart(modifiedCart.toCartEntity()) }
        }
    }

    @Test
    fun `decrease cart success - item deleted`() = runTest {
        val mockCart = Cart(
            productId = "product-456",
            productName = "Produk Habis",
            productPrice = 20000.0,
            productQuantity = 1,
            productImg = "https://example.com/product.jpg",
            productStock = 5
        )
        val modifiedCart = mockCart.copy(productQuantity = 0)
        coEvery { databaseDataSource.deleteCart(modifiedCart.toCartEntity()) } returns 1
        repository.decreaseCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.deleteCart(modifiedCart.toCartEntity()) }
        }
    }

    @Test
    fun `decrease cart error`() = runTest {
        val mockCart = Cart(
            productId = "product-789",
            productName = "Produk Error",
            productPrice = 30000.0,
            productQuantity = 2,
            productImg = "https://example.com/product.jpg",
            productStock = 8
        )

        coEvery { databaseDataSource.updateCart(any()) } throws Exception("Database error")

        repository.decreaseCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { databaseDataSource.updateCart(any()) }
        }
    }

    @Test
    fun `increase cart success`() = runTest {
        val mockCart = Cart(
            productId = "product-123",
            productName = "Produk Contoh",
            productPrice = 15000.0,
            productQuantity = 1,
            productImg = "https://example.com/product.jpg",
            productStock = 10
        )
        val modifiedCart = mockCart.copy(productQuantity = 2)
        coEvery { databaseDataSource.updateCart(modifiedCart.toCartEntity()) } returns 1
        repository.increaseCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.updateCart(modifiedCart.toCartEntity()) }
        }
    }

    @Test
    fun `increase cart error`() = runTest {
        val mockCart = Cart(
            productId = "product-456",
            productName = "Produk Error",
            productPrice = 20000.0,
            productQuantity = 3,
            productImg = "https://example.com/product.jpg",
            productStock = 5
        )

        coEvery { databaseDataSource.updateCart(any()) } throws Exception("Database error")

        repository.increaseCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { databaseDataSource.updateCart(any()) }
        }
    }

    @Test
    fun `set cart quantity success`() = runTest {
        val mockCart = Cart(
            productId = "product-123",
            productName = "Produk Contoh",
            productPrice = 15000.0,
            productQuantity = 5,
            productImg = "https://example.com/product.jpg",
            productStock = 10
        )

        coEvery { databaseDataSource.updateCart(mockCart.toCartEntity()) } returns 1

        repository.setCartQuantity(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.updateCart(mockCart.toCartEntity()) }
        }
    }

    @Test
    fun `set cart quantity error`() = runTest {
        val mockCart = Cart(
            productId = "product-456",
            productName = "Produk Gagal",
            productPrice = 20000.0,
            productQuantity = 3,
            productImg = "https://example.com/product.jpg",
            productStock = 5
        )

        coEvery { databaseDataSource.updateCart(any()) } throws Exception("Database error")

        repository.setCartQuantity(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { databaseDataSource.updateCart(any()) }
        }
    }

    @Test
    fun `delete cart success`() = runTest {
        val mockCart = Cart(
            productId = "product-123",
            productName = "Produk Hapus",
            productPrice = 25000.0,
            productQuantity = 1,
            productImg = "https://example.com/product.jpg",
            productStock = 5
        )

        coEvery { databaseDataSource.deleteCart(mockCart.toCartEntity()) } returns 1

        repository.deleteCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.deleteCart(mockCart.toCartEntity()) }
        }
    }

    @Test
    fun `delete cart error`() = runTest {
        val mockCart = Cart(
            productId = "product-789",
            productName = "Produk Error",
            productPrice = 30000.0,
            productQuantity = 2,
            productImg = "https://example.com/product.jpg",
            productStock = 4
        )

        coEvery { databaseDataSource.deleteCart(any()) } throws Exception("Database error")

        repository.deleteCart(mockCart).map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { databaseDataSource.deleteCart(any()) }
        }
    }

    @Test
    fun `delete all carts success`() = runTest {
        coEvery { databaseDataSource.deleteAll() } returns 1

        repository.deleteAll().map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Success)
            assertEquals(true, result.payload)
            coVerify { databaseDataSource.deleteAll() }
        }
    }

    @Test
    fun `delete all carts error`() = runTest {
        coEvery { databaseDataSource.deleteAll() } throws Exception("Database error")

        repository.deleteAll().map {
            delay(100)
            it
        }.test {
            delay(1000)
            val result = expectMostRecentItem()
            assertTrue(result is ResultWrapper.Error)
            coVerify { databaseDataSource.deleteAll() }
        }
    }
}

//
//    @Test
//    fun deleteCart() {
//    }
//
//    @Test
//    fun deleteAll() {
//    }
