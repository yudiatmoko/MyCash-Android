package com.iyam.mycash.data.repository

import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.outlet.OutletRequest
import com.iyam.mycash.data.network.api.model.outlet.toOutlet
import com.iyam.mycash.data.network.api.model.outlet.toOutletList
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface OutletRepository {
    suspend fun addOutlet(request: OutletRequest): Flow<ResultWrapper<Outlet>>
    suspend fun outletById(id: String): Flow<ResultWrapper<Outlet>>
    suspend fun outletsByUser(name: String?): Flow<ResultWrapper<List<Outlet>>>
    suspend fun updateOutlet(
        id: String,
        name: RequestBody,
        type: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        district: RequestBody,
        city: RequestBody,
        province: RequestBody,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<Outlet>>
}

class OutletRepositoryImpl(
    private val dataSource: ApiDataSource
) : OutletRepository {
    override suspend fun addOutlet(request: OutletRequest): Flow<ResultWrapper<Outlet>> {
        return proceedFlow {
            dataSource.addOutlet(request).data.toOutlet()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun updateOutlet(
        id: String,
        name: RequestBody,
        type: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        district: RequestBody,
        city: RequestBody,
        province: RequestBody,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<Outlet>> {
        return proceedFlow {
            dataSource.updateOutlet(
                id,
                name,
                type,
                phoneNumber,
                address,
                district,
                city,
                province,
                image
            ).data.toOutlet()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun outletById(id: String): Flow<ResultWrapper<Outlet>> {
        return proceedFlow {
            dataSource.outletById(id).data.toOutlet()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun outletsByUser(name: String?): Flow<ResultWrapper<List<Outlet>>> {
        return proceedFlow {
            dataSource.outletsByUser(name).data.toOutletList()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }
}
