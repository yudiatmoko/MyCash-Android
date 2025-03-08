package com.iyam.mycash.data.repository

import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.product.toProduct
import com.iyam.mycash.data.network.api.model.product.toProductList
import com.iyam.mycash.model.Product
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ProductRepository {
    suspend fun addProduct(
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<Product>>

    suspend fun updateProduct(
        id: String,
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<Product>>

    suspend fun productById(
        id: String
    ): Flow<ResultWrapper<Product>>

    suspend fun productsByOutlet(
        outletId: String,
        name: String?,
        slug: String?,
        status: String?
    ): Flow<ResultWrapper<List<Product>>>

    suspend fun deleteProduct(id: String): Flow<ResultWrapper<String>>
}

class ProductRepositoryImpl(
    private val dataSource: ApiDataSource
) : ProductRepository {
    override suspend fun addProduct(
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<Product>> {
        return proceedFlow {
            dataSource.addProduct(
                name,
                description,
                price,
                status,
                stock,
                categoryId,
                outletId,
                image
            ).data.toProduct()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun updateProduct(
        id: String,
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<Product>> {
        return proceedFlow {
            dataSource.updateProduct(
                id,
                name,
                description,
                price,
                status,
                stock,
                categoryId,
                outletId,
                image
            ).data.toProduct()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun productById(id: String): Flow<ResultWrapper<Product>> {
        return proceedFlow {
            dataSource.productById(id).data.toProduct()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun productsByOutlet(
        outletId: String,
        name: String?,
        slug: String?,
        status: String?
    ): Flow<ResultWrapper<List<Product>>> {
        return proceedFlow {
            dataSource.productsByOutlet(outletId, name, slug, status).data.toProductList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun deleteProduct(id: String): Flow<ResultWrapper<String>> {
        return proceedFlow {
            dataSource.deleteProduct(id).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }
}
