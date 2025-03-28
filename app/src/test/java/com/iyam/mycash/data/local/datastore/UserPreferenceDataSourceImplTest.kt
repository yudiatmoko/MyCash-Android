package com.iyam.mycash.data.local.datastore

import app.cash.turbine.test
import com.iyam.mycash.model.Auth
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.model.Session
import com.iyam.mycash.utils.PreferenceDataStoreHelper
import io.mockk.MockKAnnotations.init
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserPreferenceDataSourceImplTest {

    @MockK
    lateinit var preferenceDataStoreHelper: PreferenceDataStoreHelper

    private lateinit var userPreferenceDataSource: UserPreferenceDataSource

    @Before
    fun setUp() {
        init(this)
        userPreferenceDataSource = UserPreferenceDataSourceImpl(preferenceDataStoreHelper)
    }

    @Test
    fun setUserToken() {
        runTest {
            coEvery { preferenceDataStoreHelper.putPreference(any(), "token") } returns Unit
            val result = userPreferenceDataSource.setUserToken("token")
            coVerify { preferenceDataStoreHelper.putPreference(any(), "token") }
            TestCase.assertEquals(result, Unit)
        }
    }

    @Test
    fun getUserToken() {
        runTest {
            coEvery { preferenceDataStoreHelper.getFirstPreference(any(), "") } returns "token"
            val result = userPreferenceDataSource.getUserToken()
            coVerify { preferenceDataStoreHelper.getFirstPreference(any(), "") }
            TestCase.assertEquals(result, "token")
        }
    }

    @Test
    fun getUserTokenFlow() {
        runTest {
            coEvery {
                preferenceDataStoreHelper.getPreference(
                    any(),
                    ""
                )
            } returns flow { emit("token") }
            userPreferenceDataSource.getUserTokenFlow().test {
                val itemResult = awaitItem()
                TestCase.assertEquals("token", itemResult)
                awaitComplete()
            }
        }
    }

    @Test
    fun setAuth() = runTest {
        val auth = Auth(
            id = "123",
            email = "test@example.com",
            isVerified = true,
            name = "test",
            token = "token"
        )
        val authJson = Json.encodeToString(Auth.serializer(), auth)

        coEvery { preferenceDataStoreHelper.putPreference(any(), authJson) } returns Unit

        userPreferenceDataSource.setAuth(auth)

        coVerify { preferenceDataStoreHelper.putPreference(any(), authJson) }
    }

    @Test
    fun getAuth() = runTest {
        val auth = Auth(
            id = "123",
            email = "test@example.com",
            isVerified = true,
            name = "test",
            token = "token"
        )
        val authJson = Json.encodeToString(Auth.serializer(), auth)

        coEvery { preferenceDataStoreHelper.getFirstPreference(any(), "") } returns authJson

        val result = userPreferenceDataSource.getAuth()

        coVerify { preferenceDataStoreHelper.getFirstPreference(any(), "") }
        assertEquals(auth, result)
    }

    @Test
    fun getAuthFlow() = runTest {
        val auth = Auth(
            id = "123",
            email = "test@example.com",
            isVerified = true,
            name = "test",
            token = "token"
        )
        val authJson = Json.encodeToString(Auth.serializer(), auth)

        coEvery {
            preferenceDataStoreHelper.getPreference(
                any(),
                ""
            )
        } returns flow { emit(authJson) }

        userPreferenceDataSource.getAuthFlow().test {
            val result = awaitItem()
            assertEquals(auth, result)
            awaitComplete()
        }
    }

    @Test
    fun setOutlet() = runTest {
        val outlet = Outlet(
            id = "1",
            name = "Warung Sukses",
            type = "Retail",
            phoneNumber = "08123456789",
            address = "Jl. Merdeka No. 10",
            district = "Kecamatan A",
            city = "Jakarta",
            province = "DKI Jakarta",
            image = "https://example.com/outlet.jpg",
            userId = "12345",
            createdAt = "2024-03-15T10:00:00Z",
            updatedAt = "2024-03-16T12:00:00Z"
        )
        val outletJson = Json.encodeToString(Outlet.serializer(), outlet)

        coEvery { preferenceDataStoreHelper.putPreference(any(), outletJson) } returns Unit

        userPreferenceDataSource.setOutlet(outlet)

        coVerify { preferenceDataStoreHelper.putPreference(any(), outletJson) }
    }

    @Test
    fun getOutlet() = runTest {
        val outlet = Outlet(
            id = "1",
            name = "Warung Sukses",
            type = "Retail",
            phoneNumber = "08123456789",
            address = "Jl. Merdeka No. 10",
            district = "Kecamatan A",
            city = "Jakarta",
            province = "DKI Jakarta",
            image = "https://example.com/outlet.jpg",
            userId = "12345",
            createdAt = "2024-03-15T10:00:00Z",
            updatedAt = "2024-03-16T12:00:00Z"
        )
        val outletJson = Json.encodeToString(Outlet.serializer(), outlet)

        coEvery { preferenceDataStoreHelper.getFirstPreference(any(), "") } returns outletJson

        val result = userPreferenceDataSource.getOutlet()

        coVerify { preferenceDataStoreHelper.getFirstPreference(any(), "") }
        assertEquals(outlet, result)
    }

    @Test
    fun getOutletFlow() = runTest {
        val outlet = Outlet(
            id = "1",
            name = "Warung Sukses",
            type = "Retail",
            phoneNumber = "08123456789",
            address = "Jl. Merdeka No. 10",
            district = "Kecamatan A",
            city = "Jakarta",
            province = "DKI Jakarta",
            image = "https://example.com/outlet.jpg",
            userId = "12345",
            createdAt = "2024-03-15T10:00:00Z",
            updatedAt = "2024-03-16T12:00:00Z"
        )
        val outletJson = Json.encodeToString(Outlet.serializer(), outlet)

        coEvery {
            preferenceDataStoreHelper.getPreference(
                any(),
                ""
            )
        } returns flow { emit(outletJson) }

        userPreferenceDataSource.getOutletFlow().test {
            val result = awaitItem()
            assertEquals(outlet, result)
            awaitComplete()
        }
    }

    @Test
    fun setSession() = runTest {
        val session = Session(
            id = "S001",
            date = "2025-03-16",
            shift = "Morning",
            startingCash = 500000.0,
            totalRevenue = 1250000.0,
            checkInTime = "08:00:00",
            checkOutTime = "16:00:00",
            userId = "U12345",
            outletId = "O67890",
            createdAt = "2025-03-16T07:30:00Z",
            updatedAt = "2025-03-16T16:30:00Z"
        )
        val sessionJson = Json.encodeToString(Session.serializer(), session)

        coEvery { preferenceDataStoreHelper.putPreference(any(), sessionJson) } returns Unit

        userPreferenceDataSource.setSession(session)

        coVerify { preferenceDataStoreHelper.putPreference(any(), sessionJson) }
    }

    @Test
    fun getSession() = runTest {
        val session = Session(
            id = "S001",
            date = "2025-03-16",
            shift = "Morning",
            startingCash = 500000.0,
            totalRevenue = 1250000.0,
            checkInTime = "08:00:00",
            checkOutTime = "16:00:00",
            userId = "U12345",
            outletId = "O67890",
            createdAt = "2025-03-16T07:30:00Z",
            updatedAt = "2025-03-16T16:30:00Z"
        )
        val sessionJson = Json.encodeToString(Session.serializer(), session)

        coEvery { preferenceDataStoreHelper.getFirstPreference(any(), "") } returns sessionJson

        val result = userPreferenceDataSource.getSession()

        coVerify { preferenceDataStoreHelper.getFirstPreference(any(), "") }
        assertEquals(session, result)
    }

    @Test
    fun getSessionFlow() = runTest {
        val session = Session(
            id = "S001",
            date = "2025-03-16",
            shift = "Morning",
            startingCash = 500000.0,
            totalRevenue = 1250000.0,
            checkInTime = "08:00:00",
            checkOutTime = "16:00:00",
            userId = "U12345",
            outletId = "O67890",
            createdAt = "2025-03-16T07:30:00Z",
            updatedAt = "2025-03-16T16:30:00Z"
        )
        val sessionJson = Json.encodeToString(Session.serializer(), session)

        coEvery {
            preferenceDataStoreHelper.getPreference(
                any(),
                ""
            )
        } returns flow { emit(sessionJson) }

        userPreferenceDataSource.getSessionFlow().test {
            val result = awaitItem()
            assertEquals(session, result)
            awaitComplete()
        }
    }
}
