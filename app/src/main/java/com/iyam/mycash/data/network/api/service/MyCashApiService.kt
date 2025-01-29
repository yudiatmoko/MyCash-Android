package com.iyam.mycash.data.network.api.service

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.iyam.mycash.BuildConfig
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
import com.iyam.mycash.data.network.api.model.user.UserResponse
import com.iyam.mycash.data.network.api.model.user.login.LoginRequest
import com.iyam.mycash.data.network.api.model.user.login.LoginResponse
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpResponse
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpResponse
import com.iyam.mycash.data.network.api.model.user.register.RegisterRequest
import com.iyam.mycash.data.network.api.model.user.register.RegisterResponse
import com.iyam.mycash.data.network.api.model.user.update.PasswordUpdateRequest
import com.iyam.mycash.data.network.api.model.user.update.PasswordUpdateResponse
import com.iyam.mycash.data.network.api.model.user.update.UserUpdateResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
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

    @Multipart
    @PUT("user/{id}")
    suspend fun userUpdate(
        @Path("id") id: String,
        @Part("name") name: RequestBody,
        @Part("phoneNumber") phoneNumber: RequestBody,
        @Part image: MultipartBody.Part?
    ): UserUpdateResponse

    @PUT("user/{id}/password")
    suspend fun passwordUpdate(
        @Path("id") id: String,
        @Body request: PasswordUpdateRequest
    ): PasswordUpdateResponse

    @GET("user/{id}")
    suspend fun userById(
        @Path("id") id: String,
    ): UserResponse

    companion object {
        @JvmStatic
        operator fun invoke(
            chucker: ChuckerInterceptor,
            userPref: UserPreferenceDataSource
        ): MyCashApiService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(chucker)
                .addInterceptor(AuthorizationInterceptor(userPref))
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
