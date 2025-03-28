package com.iyam.mycash.data.repository

import com.iyam.mycash.data.local.db.datasource.DatabaseDataSource
import com.iyam.mycash.data.local.db.entity.CartEntity
import com.iyam.mycash.data.local.db.mapper.toCartEntity
import com.iyam.mycash.data.local.db.mapper.toCartList
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.product.toProductList
import com.iyam.mycash.model.Cart
import com.iyam.mycash.model.Product
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceed
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface CartRepository {
    fun getCartList(): Flow<ResultWrapper<Pair<List<Cart>, Double>>>
    suspend fun getProductWithUpdatedStock(
        outletId: String,
        name: String?,
        slug: String?,
        status: String?
    ): Flow<ResultWrapper<List<Product>>>

    suspend fun createCart(product: Product, totalQuantity: Int): Flow<ResultWrapper<Boolean>>
    suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun setCartQuantity(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>>
    suspend fun deleteAll(): Flow<ResultWrapper<Boolean>>
}

class CartRepositoryImpl(
    private val databaseDataSource: DatabaseDataSource,
    private val apiDataSource: ApiDataSource
) : CartRepository {

    override fun getCartList(): Flow<ResultWrapper<Pair<List<Cart>, Double>>> {
        return databaseDataSource.getAllCarts()
            .map {
                proceed {
                    val result = it.toCartList()
                    val totalPrice = result.sumOf {
                        val pricePerItem = it.productPrice
                        val quantity = it.productQuantity
                        pricePerItem * quantity
                    }
                    Pair(result, totalPrice)
                }
            }.map {
                if (it.payload?.first?.isEmpty() == true) {
                    ResultWrapper.Empty(it.payload)
                } else {
                    it
                }
            }.catch {
                emit(ResultWrapper.Error(Exception(it)))
            }.onStart {
                emit(ResultWrapper.Loading())
                delay(500)
            }
    }

    override suspend fun getProductWithUpdatedStock(
        outletId: String,
        name: String?,
        slug: String?,
        status: String?
    ): Flow<ResultWrapper<List<Product>>> {
        return proceedFlow {
            val apiProducts = apiDataSource.productsByOutlet(outletId, name, slug, status)
            val cartItems =
                databaseDataSource.getAllCarts().firstOrNull()?.toCartList() ?: emptyList()
            apiProducts.data.toProductList().map { apiProduct ->
                val cartItem = cartItems.find { it.productId == apiProduct.id }
                val updatedStock = if (apiProduct.stock != null) {
                    (apiProduct.stock - (cartItem?.productQuantity ?: 0)).coerceAtLeast(0)
                } else {
                    null
                }
                apiProduct.copy(stock = updatedStock)
            }
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun createCart(
        product: Product,
        totalQuantity: Int
    ): Flow<ResultWrapper<Boolean>> {
        return product.id?.let { productId ->
            proceedFlow {
                val existingCartItem =
                    databaseDataSource.getCartByProductId(productId).firstOrNull()
                if (existingCartItem != null) {
                    val updatedQuantity = existingCartItem.productQuantity + totalQuantity
                    val updatedStock = product.stock?.minus(totalQuantity) ?: Int.MAX_VALUE

                    if (updatedStock >= 0) {
                        val updatedRows = databaseDataSource.updateCart(
                            existingCartItem.copy(productQuantity = updatedQuantity)
                        )
                        return@proceedFlow updatedRows > 0
                    } else {
                        throw IllegalStateException("Not enough stock")
                    }
                } else {
                    val updatedStock = product.stock?.minus(totalQuantity) ?: Int.MAX_VALUE
                    if (updatedStock >= 0) {
                        val insertedRows = databaseDataSource.insertCart(
                            CartEntity(
                                productId = product.id,
                                productName = product.name.orEmpty(),
                                productPrice = (product.price ?: 0.0).toDouble(),
                                productImg = product.image.orEmpty(),
                                productStock = product.stock,
                                productQuantity = totalQuantity
                            )
                        )
                        return@proceedFlow insertedRows > 0
                    } else {
                        throw IllegalStateException("Not enough stock")
                    }
                }
            }
        } ?: flow {
            emit(ResultWrapper.Error(IllegalStateException("Product ID not found")))
        }
    }

    override suspend fun decreaseCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        val modifiedCart = item.copy().apply {
            productQuantity -= 1
        }
        return if (modifiedCart.productQuantity <= 0) {
            proceedFlow { databaseDataSource.deleteCart(modifiedCart.toCartEntity()) > 0 }
        } else {
            proceedFlow { databaseDataSource.updateCart(modifiedCart.toCartEntity()) > 0 }
        }
    }

    override suspend fun increaseCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        val modifiedCart = item.copy().apply {
            productQuantity += 1
        }
        return proceedFlow { databaseDataSource.updateCart(modifiedCart.toCartEntity()) > 0 }
    }

    override suspend fun setCartQuantity(item: Cart): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { databaseDataSource.updateCart(item.toCartEntity()) > 0 }
    }

    override suspend fun deleteCart(item: Cart): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { databaseDataSource.deleteCart(item.toCartEntity()) > 0 }
    }

    override suspend fun deleteAll(): Flow<ResultWrapper<Boolean>> {
        return proceedFlow { databaseDataSource.deleteAll() > 0 }
    }
}
