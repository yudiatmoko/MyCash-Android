package com.iyam.mycash.data.network.api.datasource

import com.iyam.mycash.data.network.api.model.user.UserResponse
import com.iyam.mycash.data.network.api.model.user.login.LoginRequest
import com.iyam.mycash.data.network.api.model.user.login.LoginResponse
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpResponse
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpResponse
import com.iyam.mycash.data.network.api.model.user.register.RegisterRequest
import com.iyam.mycash.data.network.api.model.user.register.RegisterResponse
import com.iyam.mycash.data.network.api.model.user.resetpassword.ResetPasswordRequest
import com.iyam.mycash.data.network.api.model.user.resetpassword.ResetPasswordResponse
import com.iyam.mycash.data.network.api.model.user.update.PasswordUpdateRequest
import com.iyam.mycash.data.network.api.model.user.update.PasswordUpdateResponse
import com.iyam.mycash.data.network.api.model.user.update.UserUpdateResponse
import com.iyam.mycash.data.network.api.service.MyCashApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiDataSource {
    suspend fun register(request: RegisterRequest): RegisterResponse
    suspend fun login(request: LoginRequest): LoginResponse
    suspend fun generateOtp(request: GenerateOtpRequest): GenerateOtpResponse
    suspend fun verifyOtp(request: VerifyOtpRequest): VerifyOtpResponse
    suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse
    suspend fun userUpdate(
        id: String,
        name: RequestBody?,
        phoneNumber: RequestBody?,
        image: MultipartBody.Part?
    ): UserUpdateResponse

    suspend fun passwordUpdate(id: String, request: PasswordUpdateRequest): PasswordUpdateResponse
    suspend fun userById(id: String): UserResponse
}

class ApiDataSourceImpl(
    private val service: MyCashApiService
) : ApiDataSource {
    override suspend fun register(request: RegisterRequest): RegisterResponse {
        return service.register(request)
    }

    override suspend fun login(request: LoginRequest): LoginResponse {
        return service.login(request)
    }

    override suspend fun generateOtp(request: GenerateOtpRequest): GenerateOtpResponse {
        return service.generateOtp(request)
    }

    override suspend fun verifyOtp(request: VerifyOtpRequest): VerifyOtpResponse {
        return service.verifyOtp(request)
    }

    override suspend fun resetPassword(request: ResetPasswordRequest): ResetPasswordResponse {
        return service.resetPassword(request)
    }

    override suspend fun userUpdate(
        id: String,
        name: RequestBody?,
        phoneNumber: RequestBody?,
        image: MultipartBody.Part?
    ): UserUpdateResponse {
        return service.userUpdate(id, name, phoneNumber, image)
    }

    override suspend fun passwordUpdate(
        id: String,
        request: PasswordUpdateRequest
    ): PasswordUpdateResponse {
        return service.passwordUpdate(id, request)
    }

    override suspend fun userById(id: String): UserResponse {
        return service.userById(id)
    }
}
