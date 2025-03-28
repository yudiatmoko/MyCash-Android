package com.iyam.mycash.data.local.db.datasource

import com.iyam.mycash.data.local.db.dao.CartDao
import com.iyam.mycash.data.local.db.entity.CartEntity
import io.mockk.MockKAnnotations.init
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DatabaseDataSourceImplTest {

    @MockK
    lateinit var cartDao: CartDao

    private lateinit var databaseDataSource: DatabaseDataSource

    @Before
    fun setUp() {
        init(this)
        databaseDataSource = DatabaseDataSourceImpl(cartDao)
    }

    @Test
    fun getAllCarts() {
        runTest {
            val mockResponse = mockk<Flow<List<CartEntity>>>(relaxed = true)
            coEvery { cartDao.getAllCarts() } returns mockResponse
            val response = cartDao.getAllCarts()
            coVerify { cartDao.getAllCarts() }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun getCartById() {
        runTest {
            val mockResponse = mockk<Flow<CartEntity>>(relaxed = true)
            coEvery { cartDao.getCartById(any()) } returns mockResponse
            val response = cartDao.getCartById(2)
            coVerify { cartDao.getCartById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun getCartByProductId() {
        runTest {
            val mockResponse = mockk<Flow<CartEntity>>(relaxed = true)
            coEvery { cartDao.getCartByProductId(any()) } returns mockResponse
            val response = cartDao.getCartByProductId("123")
            coVerify { cartDao.getCartByProductId(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun insertCart() = runTest {
        val cartEntity = CartEntity(
            productId = "123",
            productName = "Produk A",
            productPrice = 10000.0,
            productImg = "image_url",
            productStock = 10,
            productQuantity = 2
        )
        val expectedResponse = 1L
        coEvery { cartDao.insertCart(cartEntity) } returns expectedResponse
        val response = databaseDataSource.insertCart(cartEntity)
        coVerify { cartDao.insertCart(cartEntity) }
        assertEquals(expectedResponse, response)
    }

    @Test
    fun deleteCart() = runTest {
        val cartEntity = CartEntity(
            productId = "123",
            productName = "Produk A",
            productPrice = 10000.0,
            productImg = "image_url",
            productStock = 10,
            productQuantity = 2
        )
        val expectedResponse = 1
        coEvery { cartDao.deleteCart(cartEntity) } returns expectedResponse
        val response = databaseDataSource.deleteCart(cartEntity)
        coVerify { cartDao.deleteCart(cartEntity) }
        assertEquals(expectedResponse, response)
    }

    @Test
    fun updateCart() = runTest {
        val cartEntity = CartEntity(
            productId = "123",
            productName = "Produk A",
            productPrice = 10000.0,
            productImg = "image_url",
            productStock = 10,
            productQuantity = 2
        )
        val expectedResponse = 1
        coEvery { cartDao.updateCart(cartEntity) } returns expectedResponse
        val response = databaseDataSource.updateCart(cartEntity)
        coVerify { cartDao.updateCart(cartEntity) }
        assertEquals(expectedResponse, response)
    }

    @Test
    fun deleteAll() = runTest {
        val expectedResponse = 1
        coEvery { cartDao.deleteAll() } returns expectedResponse
        val response = databaseDataSource.deleteAll()
        coVerify { cartDao.deleteAll() }
        assertEquals(expectedResponse, response)
    }
}
