package com.iyam.mycash.data.network.api.datasource

import com.iyam.mycash.data.network.api.model.BaseResponse
import com.iyam.mycash.data.network.api.model.category.CategoriesResponse
import com.iyam.mycash.data.network.api.model.category.CategoryRequest
import com.iyam.mycash.data.network.api.model.category.CategoryResponse
import com.iyam.mycash.data.network.api.model.outlet.OutletRequest
import com.iyam.mycash.data.network.api.model.outlet.OutletResponse
import com.iyam.mycash.data.network.api.model.outlet.OutletsResponse
import com.iyam.mycash.data.network.api.model.product.ProductResponse
import com.iyam.mycash.data.network.api.model.product.ProductsResponse
import com.iyam.mycash.data.network.api.model.recap.RecapResponse
import com.iyam.mycash.data.network.api.model.session.RecapSessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionResponse
import com.iyam.mycash.data.network.api.model.transaction.TransactionResponse
import com.iyam.mycash.data.network.api.model.transaction.TransactionsResponse
import com.iyam.mycash.data.network.api.model.transaction.create.CreateTransactionResponse
import com.iyam.mycash.data.network.api.model.transaction.create.TransactionRequest
import com.iyam.mycash.data.network.api.model.uploadimage.UploadImageResponse
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
    suspend fun userById(id: String): UserResponse
    suspend fun userUpdate(
        id: String,
        name: RequestBody?,
        phoneNumber: RequestBody?,
        image: MultipartBody.Part?
    ): UserUpdateResponse

    suspend fun outletById(id: String): OutletResponse
    suspend fun outletsByUser(name: String?): OutletsResponse
    suspend fun addOutlet(request: OutletRequest): OutletResponse
    suspend fun updateOutlet(
        id: String,
        name: RequestBody?,
        type: RequestBody?,
        phoneNumber: RequestBody?,
        address: RequestBody?,
        district: RequestBody?,
        city: RequestBody?,
        province: RequestBody?,
        image: MultipartBody.Part?
    ): OutletResponse

    suspend fun addCategory(request: CategoryRequest): CategoryResponse
    suspend fun categoryById(id: String): CategoryResponse
    suspend fun categoriesByOutlet(outletId: String, name: String?): CategoriesResponse
    suspend fun deleteCategory(id: String): BaseResponse

    suspend fun addProduct(
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ): ProductResponse

    suspend fun updateProduct(
        id: String,
        name: RequestBody?,
        description: RequestBody?,
        price: RequestBody?,
        status: RequestBody?,
        stock: RequestBody?,
        categoryId: RequestBody?,
        outletId: RequestBody?,
        image: MultipartBody.Part?
    ): ProductResponse

    suspend fun productById(
        id: String
    ): ProductResponse

    suspend fun productsByOutlet(
        outletId: String,
        name: String? = null,
        slug: String? = null,
        status: String? = null
    ): ProductsResponse

    suspend fun deleteProduct(
        id: String
    ): BaseResponse

    suspend fun addSession(
        request: SessionRequest
    ): SessionResponse

    suspend fun updateSession(
        id: String,
        request: RecapSessionRequest
    ): SessionResponse

    suspend fun recapByOutlet(
        outletId: String,
        startDate: String?,
        endDate: String?,
        order: String?
    ): RecapResponse

    suspend fun addTransaction(
        request: TransactionRequest
    ): CreateTransactionResponse

    suspend fun voidTransaction(
        id: String
    ): BaseResponse

    suspend fun transactionById(
        id: String
    ): TransactionResponse

    suspend fun transactionsBySession(
        sessionId: String,
        number: String?,
        id: String?,
        order: String?
    ): TransactionsResponse

    suspend fun uploadSessionImage(
        sessionId: String,
        image: MultipartBody.Part
    ): UploadImageResponse

    suspend fun uploadTransactionImage(
        transactionId: String,
        image: MultipartBody.Part
    ): UploadImageResponse
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

    override suspend fun userById(id: String): UserResponse {
        return service.userById(id)
    }

    override suspend fun userUpdate(
        id: String,
        name: RequestBody?,
        phoneNumber: RequestBody?,
        image: MultipartBody.Part?
    ): UserUpdateResponse {
        return service.userUpdate(id, name, phoneNumber, image)
    }

    override suspend fun outletById(id: String): OutletResponse {
        return service.outletById(id)
    }

    override suspend fun outletsByUser(name: String?): OutletsResponse {
        return service.outletsByUser(name)
    }

    override suspend fun addOutlet(request: OutletRequest): OutletResponse {
        return service.addOutlet(request)
    }

    override suspend fun updateOutlet(
        id: String,
        name: RequestBody?,
        type: RequestBody?,
        phoneNumber: RequestBody?,
        address: RequestBody?,
        district: RequestBody?,
        city: RequestBody?,
        province: RequestBody?,
        image: MultipartBody.Part?
    ): OutletResponse {
        return service.updateOutlet(
            id,
            name,
            type,
            phoneNumber,
            address,
            district,
            city,
            province,
            image
        )
    }

    override suspend fun addCategory(request: CategoryRequest): CategoryResponse {
        return service.addCategory(request)
    }

    override suspend fun categoryById(id: String): CategoryResponse {
        return service.categoryById(id)
    }

    override suspend fun categoriesByOutlet(outletId: String, name: String?): CategoriesResponse {
        return service.categoriesByOutlet(outletId, name)
    }

    override suspend fun deleteCategory(id: String): BaseResponse {
        return service.deleteCategory(id)
    }

    override suspend fun addProduct(
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ): ProductResponse {
        return service.addProduct(
            name,
            description,
            price,
            status,
            stock,
            categoryId,
            outletId,
            image
        )
    }

    override suspend fun updateProduct(
        id: String,
        name: RequestBody?,
        description: RequestBody?,
        price: RequestBody?,
        status: RequestBody?,
        stock: RequestBody?,
        categoryId: RequestBody?,
        outletId: RequestBody?,
        image: MultipartBody.Part?
    ): ProductResponse {
        return service.updateProduct(
            id, name, description, price, status, stock, categoryId, outletId, image
        )
    }

    override suspend fun productById(id: String): ProductResponse {
        return service.productById(id)
    }

    override suspend fun productsByOutlet(
        outletId: String,
        name: String?,
        slug: String?,
        status: String?
    ): ProductsResponse {
        return service.productsByOutlet(
            outletId,
            name,
            slug,
            status
        )
    }

    override suspend fun deleteProduct(id: String): BaseResponse {
        return service.deleteProduct(id)
    }

    override suspend fun addSession(request: SessionRequest): SessionResponse {
        return service.addSession(request)
    }

    override suspend fun updateSession(id: String, request: RecapSessionRequest): SessionResponse {
        return service.updateSession(id, request)
    }

    override suspend fun recapByOutlet(
        outletId: String,
        startDate: String?,
        endDate: String?,
        order: String?
    ): RecapResponse {
        return service.recapByOutlet(outletId, startDate, endDate, order)
    }

    override suspend fun addTransaction(request: TransactionRequest): CreateTransactionResponse {
        return service.createTransaction(request)
    }

    override suspend fun voidTransaction(id: String): BaseResponse {
        return service.voidTransaction(id)
    }

    override suspend fun transactionById(id: String): TransactionResponse {
        return service.transactionById(id)
    }

    override suspend fun transactionsBySession(
        sessionId: String,
        number: String?,
        id: String?,
        order: String?
    ): TransactionsResponse {
        return service.transactionsBySession(sessionId, number, id, order)
    }

    override suspend fun uploadSessionImage(
        sessionId: String,
        image: MultipartBody.Part
    ): UploadImageResponse {
        return service.uploadSessionImage(sessionId, image)
    }

    override suspend fun uploadTransactionImage(
        transactionId: String,
        image: MultipartBody.Part
    ): UploadImageResponse {
        return service.uploadTransactionImage(transactionId, image)
    }
}
