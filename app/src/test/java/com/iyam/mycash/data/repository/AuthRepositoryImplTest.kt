package com.iyam.mycash.data.repository

import app.cash.turbine.test
import com.iyam.mycash.data.network.api.datasource.ApiDataSource
import com.iyam.mycash.data.network.api.model.user.DataAuthResponse
import com.iyam.mycash.data.network.api.model.user.DataUserResponse
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
import com.iyam.mycash.data.network.api.model.user.toUser
import com.iyam.mycash.data.network.api.model.user.update.UserUpdateResponse
import com.iyam.mycash.model.Auth
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.ResultWrapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {

    @MockK
    lateinit var datasource: ApiDataSource

    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = AuthRepositoryImpl(datasource)
    }

    @Test
    fun `register result success`() {
        val mockResponse = RegisterResponse(
            message = "message",
            status = "status",
            data = DataAuthResponse(
                token = "token",
                email = "email",
                id = "id",
                isVerified = false,
                name = "name"
            )
        )
        val mockRegisterRequest = mockk<RegisterRequest>(relaxed = true)
        coEvery { datasource.register(any()) } returns mockResponse
        runTest {
            repository.register(mockRegisterRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedAuth = Auth(
                    email = mockResponse.data.email,
                    id = mockResponse.data.id,
                    isVerified = mockResponse.data.isVerified,
                    name = mockResponse.data.name,
                    token = mockResponse.data.token
                )
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedAuth, data.payload)
                coVerify { datasource.register(mockRegisterRequest) }
            }
        }
    }

    @Test
    fun `register result loading`() {
        val mockResponse = RegisterResponse(
            message = "message",
            status = "status",
            data = DataAuthResponse(
                token = "token",
                email = "email",
                id = "id",
                isVerified = false,
                name = "name"
            )
        )
        val mockRegisterRequest = mockk<RegisterRequest>(relaxed = true)
        coEvery { datasource.register(any()) } returns mockResponse
        runTest {
            repository.register(mockRegisterRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.register(mockRegisterRequest) }
            }
        }
    }

    @Test
    fun `register result error`() {
        val mockRegisterRequest = mockk<RegisterRequest>(relaxed = true)
        coEvery { datasource.register(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.register(mockRegisterRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.register(mockRegisterRequest) }
            }
        }
    }

    @Test
    fun `login result success`() {
        val mockResponse = LoginResponse(
            message = "message",
            status = "status",
            data = DataAuthResponse(
                token = "token",
                email = "email",
                id = "id",
                isVerified = false,
                name = "name"
            )
        )
        val mockLoginRequest = mockk<LoginRequest>(relaxed = true)
        coEvery { datasource.login(any()) } returns mockResponse
        runTest {
            repository.login(mockLoginRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedAuth = Auth(
                    email = mockResponse.data.email,
                    id = mockResponse.data.id,
                    isVerified = mockResponse.data.isVerified,
                    name = mockResponse.data.name,
                    token = mockResponse.data.token
                )
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedAuth, data.payload)
                coVerify { datasource.login(mockLoginRequest) }
            }
        }
    }

    @Test
    fun `login result loading`() {
        val mockResponse = LoginResponse(
            message = "message",
            status = "status",
            data = DataAuthResponse(
                token = "token",
                email = "email",
                id = "id",
                isVerified = false,
                name = "name"
            )
        )
        val mockLoginRequest = mockk<LoginRequest>(relaxed = true)
        coEvery { datasource.login(any()) } returns mockResponse
        runTest {
            repository.login(mockLoginRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.login(mockLoginRequest) }
            }
        }
    }

    @Test
    fun `login result error`() {
        val mockLoginRequest = mockk<LoginRequest>(relaxed = true)
        coEvery { datasource.login(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.login(mockLoginRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.login(mockLoginRequest) }
            }
        }
    }

    @Test
    fun `generate otp result success`() {
        val mockResponse = GenerateOtpResponse(
            message = "message",
            status = "status"
        )
        val mockGenerateOtpRequest = mockk<GenerateOtpRequest>(relaxed = true)
        coEvery { datasource.generateOtp(any()) } returns mockResponse
        runTest {
            repository.generateOtp(mockGenerateOtpRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.message
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.generateOtp(mockGenerateOtpRequest) }
            }
        }
    }

    @Test
    fun `generate otp result loading`() {
        val mockResponse = GenerateOtpResponse(
            message = "message",
            status = "status"
        )
        val mockGenerateOtpRequest = mockk<GenerateOtpRequest>(relaxed = true)
        coEvery { datasource.generateOtp(any()) } returns mockResponse
        runTest {
            repository.generateOtp(mockGenerateOtpRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.generateOtp(mockGenerateOtpRequest) }
            }
        }
    }

    @Test
    fun `generate otp result error`() {
        val mockGenerateOtpRequest = mockk<GenerateOtpRequest>(relaxed = true)
        coEvery { datasource.generateOtp(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.generateOtp(mockGenerateOtpRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.generateOtp(mockGenerateOtpRequest) }
            }
        }
    }

    @Test
    fun `verify otp result success`() {
        val mockResponse = VerifyOtpResponse(
            message = "message",
            status = "status"
        )
        val mockVerifyOtpRequest = mockk<VerifyOtpRequest>(relaxed = true)
        coEvery { datasource.verifyOtp(any()) } returns mockResponse
        runTest {
            repository.verifyOtp(mockVerifyOtpRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.message
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.verifyOtp(mockVerifyOtpRequest) }
            }
        }
    }

    @Test
    fun `verify otp result loading`() {
        val mockResponse = VerifyOtpResponse(
            message = "message",
            status = "status"
        )
        val mockVerifyOtpRequest = mockk<VerifyOtpRequest>(relaxed = true)
        coEvery { datasource.verifyOtp(any()) } returns mockResponse
        runTest {
            repository.verifyOtp(mockVerifyOtpRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.verifyOtp(mockVerifyOtpRequest) }
            }
        }
    }

    @Test
    fun `verify otp result error`() {
        val mockVerifyOtpRequest = mockk<VerifyOtpRequest>(relaxed = true)
        coEvery { datasource.verifyOtp(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.verifyOtp(mockVerifyOtpRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.verifyOtp(mockVerifyOtpRequest) }
            }
        }
    }

    @Test
    fun `reset password result success`() {
        val mockResponse = ResetPasswordResponse(
            message = "message",
            status = "status"
        )
        val mockResetPasswordRequest = mockk<ResetPasswordRequest>(relaxed = true)
        coEvery { datasource.resetPassword(any()) } returns mockResponse
        runTest {
            repository.resetPassword(mockResetPasswordRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.message
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.resetPassword(mockResetPasswordRequest) }
            }
        }
    }

    @Test
    fun `reset password result loading`() {
        val mockResponse = ResetPasswordResponse(
            message = "message",
            status = "status"
        )
        val mockResetPasswordRequest = mockk<ResetPasswordRequest>(relaxed = true)
        coEvery { datasource.resetPassword(any()) } returns mockResponse
        runTest {
            repository.resetPassword(mockResetPasswordRequest).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.resetPassword(mockResetPasswordRequest) }
            }
        }
    }

    @Test
    fun `reset password result error`() {
        val mockResetPasswordRequest = mockk<ResetPasswordRequest>(relaxed = true)
        coEvery { datasource.resetPassword(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.resetPassword(mockResetPasswordRequest).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.resetPassword(mockResetPasswordRequest) }
            }
        }
    }

    @Test
    fun `user update result success`() {
        val mockResponse = UserUpdateResponse(
            message = "message",
            status = "status",
            data = DataUserResponse(
                createdAt = "2025-03-16T12:00:00Z",
                email = "user@example.com",
                id = "12345",
                image = "https://example.com/profile.jpg",
                isVerified = true,
                name = "John Doe",
                phoneNumber = "+62123456789",
                updatedAt = "2025-03-16T12:30:00Z"
            )
        )
        val mockId = "123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockPhoneNumber = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery { datasource.userUpdate(any(), any(), any(), any()) } returns mockResponse
        runTest {
            repository.userUpdate(mockId, mockName, mockPhoneNumber, mockImage).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toUser()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.userUpdate(mockId, mockName, mockPhoneNumber, mockImage) }
            }
        }
    }

    @Test
    fun `user update result loading`() {
        val mockResponse = UserUpdateResponse(
            message = "message",
            status = "status",
            data = DataUserResponse(
                createdAt = "2025-03-16T12:00:00Z",
                email = "user@example.com",
                id = "12345",
                image = "https://example.com/profile.jpg",
                isVerified = true,
                name = "John Doe",
                phoneNumber = "+62123456789",
                updatedAt = "2025-03-16T12:30:00Z"
            )
        )
        val mockId = "123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockPhoneNumber = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery { datasource.userUpdate(any(), any(), any(), any()) } returns mockResponse
        runTest {
            repository.userUpdate(mockId, mockName, mockPhoneNumber, mockImage).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.userUpdate(mockId, mockName, mockPhoneNumber, mockImage) }
            }
        }
    }

    @Test
    fun `user update result error`() {
        val mockId = "123"
        val mockName = mockk<RequestBody>(relaxed = true)
        val mockPhoneNumber = mockk<RequestBody>(relaxed = true)
        val mockImage = mockk<MultipartBody.Part>(relaxed = true)
        coEvery {
            datasource.userUpdate(
                any(),
                any(),
                any(),
                any()
            )
        } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.userUpdate(mockId, mockName, mockPhoneNumber, mockImage).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.userUpdate(mockId, mockName, mockPhoneNumber, mockImage) }
            }
        }
    }

    @Test
    fun `user by id result success`() {
        val mockResponse = UserResponse(
            message = "message",
            status = "status",
            data = DataUserResponse(
                createdAt = "2025-03-16T12:00:00Z",
                email = "user@example.com",
                id = "12345",
                image = "https://example.com/profile.jpg",
                isVerified = true,
                name = "John Doe",
                phoneNumber = "+62123456789",
                updatedAt = "2025-03-16T12:30:00Z"
            )
        )
        val mockId = "123"
        coEvery { datasource.userById(any()) } returns mockResponse
        runTest {
            repository.userById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                val expectedMessage = mockResponse.data.toUser()
                TestCase.assertTrue(data is ResultWrapper.Success)
                TestCase.assertEquals(expectedMessage, data.payload)
                coVerify { datasource.userById(mockId) }
            }
        }
    }

    @Test
    fun `user by id result loading`() {
        val mockResponse = UserResponse(
            message = "message",
            status = "status",
            data = DataUserResponse(
                createdAt = "2025-03-16T12:00:00Z",
                email = "user@example.com",
                id = "12345",
                image = "https://example.com/profile.jpg",
                isVerified = true,
                name = "John Doe",
                phoneNumber = "+62123456789",
                updatedAt = "2025-03-16T12:30:00Z"
            )
        )
        val mockId = "123"
        coEvery { datasource.userById(any()) } returns mockResponse
        runTest {
            repository.userById(mockId).map {
                delay(100)
                it
            }.test {
                delay(800)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Loading)
                coVerify { datasource.userById(mockId) }
            }
        }
    }

    @Test
    fun `user by id result error`() {
        val mockId = "123"
        coEvery { datasource.userById(any()) } throws ApiException("Api Exception", 500, null)
        runTest {
            repository.userById(mockId).map {
                delay(100)
                it
            }.test {
                delay(1000)
                val data = expectMostRecentItem()
                TestCase.assertTrue(data is ResultWrapper.Error)
                coVerify { datasource.userById(mockId) }
            }
        }
    }
}
