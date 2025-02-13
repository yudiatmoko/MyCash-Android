package com.iyam.mycash.data.repository

import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.recap.RecapResponse
import com.iyam.mycash.data.network.api.model.session.RecapSessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionRequest
import com.iyam.mycash.data.network.api.model.session.toSession
import com.iyam.mycash.data.network.api.model.session.toSessionList
import com.iyam.mycash.model.Session
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart

interface SessionRepository {
    suspend fun addSession(request: SessionRequest): Flow<ResultWrapper<Session>>
    suspend fun updateSession(
        id: String,
        request: RecapSessionRequest
    ): Flow<ResultWrapper<Session>>

    suspend fun sessionById(id: String): Flow<ResultWrapper<Session>>
    suspend fun sessionsByOutlet(outletId: String): Flow<ResultWrapper<List<Session>>>
    suspend fun deleteSession(id: String): Flow<ResultWrapper<String>>
    suspend fun recapByOutlet(
        outletId: String,
        startDate: String?,
        endDate: String?,
        order: String?
    ): Flow<ResultWrapper<RecapResponse>>
}

class SessionRepositoryImpl(
    private val dataSource: ApiDataSource
) : SessionRepository {
    override suspend fun addSession(request: SessionRequest): Flow<ResultWrapper<Session>> {
        return proceedFlow {
            dataSource.addSession(request).data.toSession()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun updateSession(
        id: String,
        request: RecapSessionRequest
    ): Flow<ResultWrapper<Session>> {
        return proceedFlow {
            dataSource.updateSession(id, request).data.toSession()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun sessionById(id: String): Flow<ResultWrapper<Session>> {
        return proceedFlow {
            dataSource.sessionById(id).data.toSession()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun sessionsByOutlet(outletId: String): Flow<ResultWrapper<List<Session>>> {
        return proceedFlow {
            dataSource.sessionsByOutlet(outletId).data.toSessionList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun deleteSession(id: String): Flow<ResultWrapper<String>> {
        return proceedFlow {
            dataSource.deleteSession(id).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }

    override suspend fun recapByOutlet(
        outletId: String,
        startDate: String?,
        endDate: String?,
        order: String?
    ): Flow<ResultWrapper<RecapResponse>> {
        return proceedFlow {
            dataSource.recapByOutlet(outletId, startDate, endDate, order)
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
        }
    }
}
