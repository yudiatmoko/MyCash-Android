package com.iyam.mycash.data.network.api.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.iyam.mycash.BuildConfig
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
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
import com.iyam.mycash.data.network.api.model.session.SessionsResponse
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
import com.iyam.mycash.data.network.api.model.user.update.PasswordUpdateRequest
import com.iyam.mycash.data.network.api.model.user.update.PasswordUpdateResponse
import com.iyam.mycash.data.network.api.model.user.update.UserUpdateResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface MyCashApiService {

    @POST("user/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("user/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("user/generate-otp")
    suspend fun generateOtp(@Body request: GenerateOtpRequest): GenerateOtpResponse

    @POST("user/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): VerifyOtpResponse

    @POST("user/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): ResetPasswordResponse

    @Multipart
    @PUT("user/{id}")
    suspend fun userUpdate(
        @Path("id") id: String,
        @Part("name") name: RequestBody?,
        @Part("phoneNumber") phoneNumber: RequestBody?,
        @Part image: MultipartBody.Part?
    ): UserUpdateResponse

    @PUT("user/{id}/password")
    suspend fun passwordUpdate(
        @Path("id") id: String,
        @Body request: PasswordUpdateRequest
    ): PasswordUpdateResponse

    @GET("user/{id}")
    suspend fun userById(
        @Path("id") id: String
    ): UserResponse

    @POST("outlet")
    suspend fun addOutlet(
        @Body request: OutletRequest
    ): OutletResponse

    @Multipart
    @PUT("outlet/{id}")
    suspend fun updateOutlet(
        @Path("id") id: String,
        @Part("name") name: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part("phoneNumber") phoneNumber: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part("district") district: RequestBody?,
        @Part("city") city: RequestBody?,
        @Part("province") province: RequestBody?,
        @Part image: MultipartBody.Part?
    ): OutletResponse

    @GET("outlet/{id}")
    suspend fun outletById(
        @Path("id") id: String
    ): OutletResponse

    @GET("outlet/user")
    suspend fun outletsByUser(
        @Query("name") search: String? = null
    ): OutletsResponse

    @POST("category")
    suspend fun addCategory(
        @Body request: CategoryRequest
    ): CategoryResponse

    @GET("category/{id}")
    suspend fun categoryById(
        @Path("id") id: String
    ): CategoryResponse

    @GET("category/outlet/{outletId}")
    suspend fun categoriesByOutlet(
        @Path("outletId") outletId: String,
        @Query("name") name: String? = null
    ): CategoriesResponse

    @DELETE("category/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String
    ): BaseResponse

    @Multipart
    @POST("product")
    suspend fun addProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("status") status: RequestBody,
        @Part("stock") stock: RequestBody?,
        @Part("categoryId") categoryId: RequestBody,
        @Part("outletId") outletId: RequestBody,
        @Part image: MultipartBody.Part?
    ): ProductResponse

    @Multipart
    @PUT("product/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("price") price: RequestBody?,
        @Part("status") status: RequestBody?,
        @Part("stock") stock: RequestBody?,
        @Part("categoryId") categoryId: RequestBody?,
        @Part("outletId") outletId: RequestBody?,
        @Part image: MultipartBody.Part?
    ): ProductResponse

    @GET("product/{id}")
    suspend fun productById(
        @Path("id") id: String
    ): ProductResponse

    @GET("product/outlet/{outletId}")
    suspend fun productsByOutlet(
        @Path("outletId") outletId: String,
        @Query("name") name: String? = null,
        @Query("slug") slug: String? = null
    ): ProductsResponse

    @DELETE("product/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): BaseResponse

    @POST("session")
    suspend fun addSession(
        @Body request: SessionRequest
    ): SessionResponse

    @PUT("session/{id}")
    suspend fun updateSession(
        @Path("id") id: String,
        @Body request: RecapSessionRequest
    ): SessionResponse

    @GET("session/{id}")
    suspend fun sessionById(
        @Path("id") id: String
    ): SessionResponse

    @GET("session/outlet/{outletId}")
    suspend fun sessionsByOutlet(
        @Path("outletId") outletId: String
    ): SessionsResponse

    @DELETE("session/{id}")
    suspend fun deleteSession(
        @Path("id") id: String
    ): BaseResponse

    @GET("recap/{outletId}")
    suspend fun recapByOutlet(
        @Path("outletId") outletId: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null,
        @Query("order") order: String? = null
    ): RecapResponse

    @Multipart
    @POST("receipt-image/session/{sessionId}")
    suspend fun uploadSessionImage(
        @Path("sessionId") sessionId: String,
        @Part image: MultipartBody.Part
    ): UploadImageResponse

    @POST("receipt-image/transaction/{transactionId}")
    suspend fun uploadTransactionImage(
        @Path("transactionId") transactionId: String,
        @Part image: MultipartBody.Part
    ): UploadImageResponse

    companion object {
        @JvmStatic
        operator fun invoke(
            chucker: ChuckerInterceptor,
            userPref: UserPreferenceDataSource
        ): MyCashApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(userPref))
                .addInterceptor(chucker)
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
            return retrofit.create(MyCashApiService::class.java)
        }
    }
}
