package com.iyam.mycash.data.repository

import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.user.login.LoginRequest
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpRequest
import com.iyam.mycash.data.network.api.model.user.register.RegisterRequest
import com.iyam.mycash.data.network.api.model.user.resetpassword.ResetPasswordRequest
import com.iyam.mycash.data.network.api.model.user.toAuth
import com.iyam.mycash.data.network.api.model.user.toUser
import com.iyam.mycash.model.Auth
import com.iyam.mycash.model.User
import com.iyam.mycash.utils.ResultWrapper
import com.iyam.mycash.utils.proceedFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {
    suspend fun register(request: RegisterRequest): Flow<ResultWrapper<Auth>>
    suspend fun login(request: LoginRequest): Flow<ResultWrapper<Auth>>
    suspend fun generateOtp(request: GenerateOtpRequest): Flow<ResultWrapper<String>>
    suspend fun verifyOtp(request: VerifyOtpRequest): Flow<ResultWrapper<String>>
    suspend fun resetPassword(request: ResetPasswordRequest): Flow<ResultWrapper<String>>
    suspend fun userUpdate(
        id: String,
        name: RequestBody?,
        phoneNumber: RequestBody?,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<User>>

    suspend fun userById(id: String): Flow<ResultWrapper<User>>
}

class AuthRepositoryImpl(
    private val dataSource: ApiDataSource
) : AuthRepository {
    override suspend fun register(request: RegisterRequest): Flow<ResultWrapper<Auth>> {
        return proceedFlow {
            dataSource.register(request).data.toAuth()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun login(request: LoginRequest): Flow<ResultWrapper<Auth>> {
        return proceedFlow {
            dataSource.login(request).data.toAuth()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun generateOtp(request: GenerateOtpRequest): Flow<ResultWrapper<String>> {
        return proceedFlow {
            dataSource.generateOtp(request).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun verifyOtp(request: VerifyOtpRequest): Flow<ResultWrapper<String>> {
        return proceedFlow {
            dataSource.verifyOtp(request).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): Flow<ResultWrapper<String>> {
        return proceedFlow {
            dataSource.resetPassword(request).message
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun userUpdate(
        id: String,
        name: RequestBody?,
        phoneNumber: RequestBody?,
        image: MultipartBody.Part?
    ): Flow<ResultWrapper<User>> {
        return proceedFlow {
            dataSource.userUpdate(id, name, phoneNumber, image).data.toUser()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }

    override suspend fun userById(id: String): Flow<ResultWrapper<User>> {
        return proceedFlow {
            dataSource.userById(id).data.toUser()
        }.catch {
            emit(ResultWrapper.Error(Exception(it)))
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(500)
        }
    }
}
