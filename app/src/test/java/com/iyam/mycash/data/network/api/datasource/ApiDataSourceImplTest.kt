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
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ApiDataSourceImplTest {

    @MockK
    lateinit var service: MyCashApiService

    private lateinit var dataSource: ApiDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dataSource = ApiDataSourceImpl(service)
    }

    @Test
    fun register() {
        runTest {
            val mockResponse = mockk<RegisterResponse>(relaxed = true)
            coEvery { service.register(any()) } returns mockResponse
            val mockRequestRegister = mockk<RegisterRequest>(relaxed = true)
            val response = dataSource.register(mockRequestRegister)
            coVerify { service.register(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun login() {
        runTest {
            val mockResponse = mockk<LoginResponse>(relaxed = true)
            coEvery { service.login(any()) } returns mockResponse
            val mockRequestLogin = mockk<LoginRequest>(relaxed = true)
            val response = dataSource.login(mockRequestLogin)
            coVerify { service.login(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun generateOtp() {
        runTest {
            val mockResponse = mockk<GenerateOtpResponse>(relaxed = true)
            coEvery { service.generateOtp(any()) } returns mockResponse
            val mockRequestGenerateOtp = mockk<GenerateOtpRequest>(relaxed = true)
            val response = dataSource.generateOtp(mockRequestGenerateOtp)
            coVerify { service.generateOtp(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun verifyOtp() {
        runTest {
            val mockResponse = mockk<VerifyOtpResponse>(relaxed = true)
            coEvery { service.verifyOtp(any()) } returns mockResponse
            val mockRequestVerifyOtp = mockk<VerifyOtpRequest>(relaxed = true)
            val response = dataSource.verifyOtp(mockRequestVerifyOtp)
            coVerify { service.verifyOtp(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun resetPassword() {
        runTest {
            val mockResponse = mockk<ResetPasswordResponse>(relaxed = true)
            coEvery { service.resetPassword(any()) } returns mockResponse
            val mockRequestResetPassword = mockk<ResetPasswordRequest>(relaxed = true)
            val response = dataSource.resetPassword(mockRequestResetPassword)
            coVerify { service.resetPassword(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun userById() {
        runTest {
            val mockResponse = mockk<UserResponse>(relaxed = true)
            coEvery { service.userById(any()) } returns mockResponse
            val response = dataSource.userById("1")
            coVerify { service.userById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun userUpdate() = runTest {
        val mockResponse = mockk<UserUpdateResponse>(relaxed = true)
        coEvery { service.userUpdate(any(), any(), any(), any()) } returns mockResponse
        val id = "123"
        val name = "Ilham Yudiatmoko".toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumber = "08123456789".toRequestBody("text/plain".toMediaTypeOrNull())
        val file = "dummy_image".toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", "photo.jpg", file)
        val response = dataSource.userUpdate(id, name, phoneNumber, imagePart)
        coVerify { service.userUpdate(id, name, phoneNumber, imagePart) }
        assertEquals(mockResponse, response)
    }

    @Test
    fun outletById() {
        runTest {
            val mockResponse = mockk<OutletResponse>(relaxed = true)
            coEvery { service.outletById(any()) } returns mockResponse
            val response = dataSource.outletById("1")
            coVerify { service.outletById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun outletsByUser() {
        runTest {
            val mockResponse = mockk<OutletsResponse>(relaxed = true)
            coEvery { service.outletsByUser(any()) } returns mockResponse
            val response = dataSource.outletsByUser("1")
            coVerify { service.outletsByUser(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun addOutlet() {
        runTest {
            val mockResponse = mockk<OutletResponse>(relaxed = true)
            coEvery { service.addOutlet(any()) } returns mockResponse
            val mockOutletRequest = mockk<OutletRequest>(relaxed = true)
            val response = dataSource.addOutlet(mockOutletRequest)
            coVerify { service.addOutlet(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun updateOutlet() = runTest {
        val mockResponse = mockk<OutletResponse>(relaxed = true)
        coEvery {
            service.updateOutlet(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns mockResponse
        val id = "outlet_123"
        val name = "Outlet XYZ".toRequestBody("text/plain".toMediaTypeOrNull())
        val type = "Retail".toRequestBody("text/plain".toMediaTypeOrNull())
        val phoneNumber = "08123456789".toRequestBody("text/plain".toMediaTypeOrNull())
        val address = "Jl. Contoh No. 123".toRequestBody("text/plain".toMediaTypeOrNull())
        val district = "Kecamatan ABC".toRequestBody("text/plain".toMediaTypeOrNull())
        val city = "Kota XYZ".toRequestBody("text/plain".toMediaTypeOrNull())
        val province = "Provinsi LMN".toRequestBody("text/plain".toMediaTypeOrNull())
        val file = "dummy_image".toRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", "outlet.jpg", file)
        val response = dataSource.updateOutlet(
            id,
            name,
            type,
            phoneNumber,
            address,
            district,
            city,
            province,
            imagePart
        )
        coVerify {
            service.updateOutlet(
                id,
                name,
                type,
                phoneNumber,
                address,
                district,
                city,
                province,
                imagePart
            )
        }
        assertEquals(mockResponse, response)
    }

    @Test
    fun addCategory() {
        runTest {
            val mockResponse = mockk<CategoryResponse>(relaxed = true)
            coEvery { service.addCategory(any()) } returns mockResponse
            val mockCategoryRequest = mockk<CategoryRequest>(relaxed = true)
            val response = dataSource.addCategory(mockCategoryRequest)
            coVerify { service.addCategory(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun categoryById() {
        runTest {
            val mockResponse = mockk<CategoryResponse>(relaxed = true)
            coEvery { service.categoryById(any()) } returns mockResponse
            val response = dataSource.categoryById("1")
            coVerify { service.categoryById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun categoriesByOutlet() {
        runTest {
            val mockResponse = mockk<CategoriesResponse>(relaxed = true)
            coEvery { service.categoriesByOutlet(any(), any()) } returns mockResponse
            val response = dataSource.categoriesByOutlet("1", "name")
            coVerify { service.categoriesByOutlet(any(), any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun deleteCategory() {
        runTest {
            val mockResponse = mockk<BaseResponse>(relaxed = true)
            coEvery { service.deleteCategory(any()) } returns mockResponse
            val response = dataSource.deleteCategory("1")
            coVerify { service.deleteCategory(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun addProduct() {
        runTest {
            val mockResponse = mockk<ProductResponse>(relaxed = true)
            coEvery {
                service.addProduct(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns mockResponse
            val name = "Produk ABC".toRequestBody("text/plain".toMediaTypeOrNull())
            val description = "Deskripsi produk".toRequestBody("text/plain".toMediaTypeOrNull())
            val price = "10000".toRequestBody("text/plain".toMediaTypeOrNull())
            val status = "available".toRequestBody("text/plain".toMediaTypeOrNull())
            val stock = "50".toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryId = "1".toRequestBody("text/plain".toMediaTypeOrNull())
            val outletId = "10".toRequestBody("text/plain".toMediaTypeOrNull())
            val file = "dummy_image".toRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", "product.jpg", file)
            val response = dataSource.addProduct(
                name,
                description,
                price,
                status,
                stock,
                categoryId,
                outletId,
                imagePart
            )
            coVerify {
                service.addProduct(
                    name,
                    description,
                    price,
                    status,
                    stock,
                    categoryId,
                    outletId,
                    imagePart
                )
            }
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun updateProduct() {
        runTest {
            val mockResponse = mockk<ProductResponse>(relaxed = true)
            coEvery {
                service.updateProduct(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            } returns mockResponse
            val id = "1"
            val name = "Produk ABC".toRequestBody("text/plain".toMediaTypeOrNull())
            val description = "Deskripsi produk".toRequestBody("text/plain".toMediaTypeOrNull())
            val price = "10000".toRequestBody("text/plain".toMediaTypeOrNull())
            val status = "available".toRequestBody("text/plain".toMediaTypeOrNull())
            val stock = "50".toRequestBody("text/plain".toMediaTypeOrNull())
            val categoryId = "1".toRequestBody("text/plain".toMediaTypeOrNull())
            val outletId = "10".toRequestBody("text/plain".toMediaTypeOrNull())
            val file = "dummy_image".toRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", "product.jpg", file)
            val response = dataSource.updateProduct(
                id,
                name,
                description,
                price,
                status,
                stock,
                categoryId,
                outletId,
                imagePart
            )
            coVerify {
                service.updateProduct(
                    id,
                    name,
                    description,
                    price,
                    status,
                    stock,
                    categoryId,
                    outletId,
                    imagePart
                )
            }
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun productById() {
        runTest {
            val mockResponse = mockk<ProductResponse>(relaxed = true)
            coEvery { service.productById(any()) } returns mockResponse
            val response = dataSource.productById("1")
            coVerify { service.productById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun productsByOutlet() {
        runTest {
            val mockResponse = mockk<ProductsResponse>(relaxed = true)
            coEvery { service.productsByOutlet(any(), any()) } returns mockResponse
            val response = dataSource.productsByOutlet("1", "name")
            coVerify { service.productsByOutlet(any(), any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun deleteProduct() {
        runTest {
            val mockResponse = mockk<BaseResponse>(relaxed = true)
            coEvery { service.deleteProduct(any()) } returns mockResponse
            val response = dataSource.deleteProduct("1")
            coVerify { service.deleteProduct(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun addSession() {
        runTest {
            val mockResponse = mockk<SessionResponse>(relaxed = true)
            coEvery { service.addSession(any()) } returns mockResponse
            val mockSessionRequest = mockk<SessionRequest>(relaxed = true)
            val response = dataSource.addSession(mockSessionRequest)
            coVerify { service.addSession(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun updateSession() {
        runTest {
            val mockResponse = mockk<SessionResponse>(relaxed = true)
            coEvery { service.updateSession(any(), any()) } returns mockResponse
            val mockSessionRequest = mockk<RecapSessionRequest>(relaxed = true)
            val response = dataSource.updateSession("id", mockSessionRequest)
            coVerify { service.updateSession(any(), any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun recapByOutlet() {
        runTest {
            val mockResponse = mockk<RecapResponse>(relaxed = true)
            coEvery { service.recapByOutlet(any(), any(), any(), any()) } returns mockResponse
            val outletId = "10"
            val startDate = "2024-01-01"
            val endDate = "2024-01-31"
            val order = "asc"
            val response = dataSource.recapByOutlet(outletId, startDate, endDate, order)
            coVerify { service.recapByOutlet(any(), any(), any(), any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun addTransaction() {
        runTest {
            val mockResponse = mockk<CreateTransactionResponse>(relaxed = true)
            coEvery { service.createTransaction(any()) } returns mockResponse
            val mockRequest = mockk<TransactionRequest>(relaxed = true)
            val response = dataSource.addTransaction(mockRequest)
            coVerify { service.createTransaction(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun voidTransaction() {
        runTest {
            val mockResponse = mockk<BaseResponse>(relaxed = true)
            coEvery { service.voidTransaction(any()) } returns mockResponse
            val response = dataSource.voidTransaction("id")
            coVerify { service.voidTransaction(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun transactionById() {
        runTest {
            val mockResponse = mockk<TransactionResponse>(relaxed = true)
            coEvery { service.transactionById(any()) } returns mockResponse
            val response = dataSource.transactionById("id")
            coVerify { service.transactionById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun transactionsBySession() {
        runTest {
            val mockResponse = mockk<TransactionResponse>(relaxed = true)
            coEvery { service.transactionById(any()) } returns mockResponse
            val response = dataSource.transactionById("id")
            coVerify { service.transactionById(any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun uploadSessionImage() {
        runTest {
            val mockResponse = mockk<UploadImageResponse>(relaxed = true)
            coEvery { service.uploadSessionImage(any(), any()) } returns mockResponse
            val file = "dummy_image".toRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", "product.jpg", file)
            val response = dataSource.uploadSessionImage("id", imagePart)
            coVerify { service.uploadSessionImage(any(), any()) }
            assertEquals(response, mockResponse)
        }
    }

    @Test
    fun uploadTransactionImage() {
        runTest {
            val mockResponse = mockk<UploadImageResponse>(relaxed = true)
            coEvery { service.uploadTransactionImage(any(), any()) } returns mockResponse
            val file = "dummy_image".toRequestBody("image/jpeg".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", "product.jpg", file)
            val response = dataSource.uploadTransactionImage("id", imagePart)
            coVerify { service.uploadTransactionImage(any(), any()) }
            assertEquals(response, mockResponse)
        }
    }
}
