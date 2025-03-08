package com.iyam.mycash.data.local.db.datasource

import com.iyam.mycash.data.local.db.dao.CartDao
import com.iyam.mycash.data.local.db.entity.CartEntity
import kotlinx.coroutines.flow.Flow

interface DatabaseDataSource {
    fun getAllCarts(): Flow<List<CartEntity>>
    fun getCartById(cartId: Int): Flow<CartEntity>
    fun getCartByProductId(productId: String): Flow<CartEntity?>
    suspend fun insertCart(cart: CartEntity): Long
    suspend fun deleteCart(cart: CartEntity): Int
    suspend fun updateCart(cart: CartEntity): Int
    suspend fun deleteAll(): Int
}

class DatabaseDataSourceImpl(private val cartDao: CartDao) : DatabaseDataSource {
    override fun getAllCarts(): Flow<List<CartEntity>> {
        return cartDao.getAllCarts()
    }

    override fun getCartById(cartId: Int): Flow<CartEntity> {
        return cartDao.getCartById(cartId)
    }

    override fun getCartByProductId(productId: String): Flow<CartEntity?> {
        return cartDao.getCartByProductId(productId)
    }

    override suspend fun insertCart(cart: CartEntity): Long {
        return cartDao.insertCart(cart)
    }

    override suspend fun deleteCart(cart: CartEntity): Int {
        return cartDao.deleteCart(cart)
    }

    override suspend fun updateCart(cart: CartEntity): Int {
        return cartDao.updateCart(cart)
    }

    override suspend fun deleteAll(): Int {
        return cartDao.deleteAll()
    }
}
