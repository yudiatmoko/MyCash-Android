package com.iyam.mycash.data.repository

import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.transaction.create.TransactionRequest
import com.iyam.mycash.data.network.api.model.transaction.toTransaction
import com.iyam.mycash.data.network.api.model.transaction.toTransactionList
import com.iyam.mycash.model.Transaction
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody

interface TransactionRepository {
    suspend fun addTransaction(request: TransactionRequest): Flow<ResultWrapper<String>>
    suspend fun transactionById(id: String): Flow<ResultWrapper<Transaction>>
    suspend fun transactionList(
        sessionId: String,
        number: String?,
        order: String?
    ): Flow<ResultWrapper<List<Transaction>>>

    suspend fun voidTransaction(id: String): Flow<ResultWrapper<String>>
    suspend fun uploadTransactionImage(
        transactionId: String,
        image: MultipartBody.Part
    ): Flow<ResultWrapper<String>>
}

class TransactionRepositoryImpl(
    private val apiDataSource: ApiDataSource
) : TransactionRepository {
    override suspend fun addTransaction(request: TransactionRequest): Flow<ResultWrapper<String>> {
        return proceedFlow {
            apiDataSource.addTransaction(request).data.transactionId
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun transactionById(id: String): Flow<ResultWrapper<Transaction>> {
        return proceedFlow {
            apiDataSource.transactionById(id).data.toTransaction()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun transactionList(
        sessionId: String,
        number: String?,
        order: String?
    ): Flow<ResultWrapper<List<Transaction>>> {
        return proceedFlow {
            apiDataSource.transactionsBySession(sessionId, number, order).data.toTransactionList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun voidTransaction(id: String): Flow<ResultWrapper<String>> {
        return proceedFlow {
            apiDataSource.voidTransaction(id).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun uploadTransactionImage(
        transactionId: String,
        image: MultipartBody.Part
    ): Flow<ResultWrapper<String>> {
        return proceedFlow {
            apiDataSource.uploadTransactionImage(transactionId, image).data.image
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }
}
