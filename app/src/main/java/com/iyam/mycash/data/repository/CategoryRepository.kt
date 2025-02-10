package com.iyam.mycash.data.repository

import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.category.CategoryRequest
import com.iyam.mycash.data.network.api.model.category.toCategory
import com.iyam.mycash.data.network.api.model.category.toCategoryList
import com.iyam.mycash.model.Category
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

interface CategoryRepository {
    suspend fun addCategory(request: CategoryRequest): Flow<ResultWrapper<Category>>
    suspend fun categoryById(id: String): Flow<ResultWrapper<Category>>
    suspend fun categoriesByOutlet(outletId: String, name: String?): Flow<ResultWrapper<List<Category>>>
    suspend fun deleteCategory(id: String): Flow<ResultWrapper<String>>
}

class CategoryRepositoryImpl(
    private val dataSource: ApiDataSource
) : CategoryRepository {
    override suspend fun addCategory(request: CategoryRequest): Flow<ResultWrapper<Category>> {
        return proceedFlow {
            dataSource.addCategory(request).data.toCategory()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun categoryById(id: String): Flow<ResultWrapper<Category>> {
        return proceedFlow {
            dataSource.categoryById(id).data.toCategory()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun categoriesByOutlet(outletId: String, name: String?): Flow<ResultWrapper<List<Category>>> {
        return proceedFlow {
            dataSource.categoriesByOutlet(outletId, name).data.toCategoryList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun deleteCategory(id: String): Flow<ResultWrapper<String>> {
        return proceedFlow {
            dataSource.deleteCategory(id).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }
}
