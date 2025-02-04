package com.iyam.mycash.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey
import com.iyam.mycash.model.Auth
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.utils.PreferenceDataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

interface UserPreferenceDataSource {
    suspend fun setUserToken(token: String)
    suspend fun getUserToken(): String
    suspend fun removeUserToken()
    fun getUserTokenFlow(): Flow<String>

    suspend fun setAuth(auth: Auth)
    suspend fun getAuth(): Auth?
    suspend fun removeAuth()
    fun getAuthFlow(): Flow<Auth?>

    suspend fun setOutlet(outlet: Outlet)
    suspend fun getOutlet(): Outlet?
    suspend fun removeOutlet()
    fun getOutletFlow(): Flow<Outlet?>
}

class UserPreferenceDataSourceImpl(
    private val helper: PreferenceDataStoreHelper
) : UserPreferenceDataSource {
    override suspend fun setUserToken(token: String) {
        helper.putPreference(USER_TOKEN_PREF, token)
    }

    override suspend fun getUserToken(): String {
        return helper.getFirstPreference(USER_TOKEN_PREF, "")
    }

    override suspend fun removeUserToken() {
        helper.removePreference(USER_TOKEN_PREF)
    }

    override fun getUserTokenFlow(): Flow<String> {
        return helper.getPreference(USER_TOKEN_PREF, "")
    }

    override suspend fun setAuth(auth: Auth) {
        val authJson = Json.encodeToString(auth)
        helper.putPreference(AUTH_PREF, authJson)
    }

    override suspend fun getAuth(): Auth? {
        val authJson = helper.getFirstPreference(AUTH_PREF, "")
        return if (authJson.isNotEmpty()) Json.decodeFromString(authJson) else null
    }

    override suspend fun removeAuth() {
        helper.removePreference(AUTH_PREF)
    }

    override fun getAuthFlow(): Flow<Auth?> {
        return helper.getPreference(AUTH_PREF, "").map { auth ->
            if (auth.isNotEmpty()) Json.decodeFromString<Auth>(auth) else null
        }
    }

    override suspend fun setOutlet(outlet: Outlet) {
        val outletJson = Json.encodeToString(outlet)
        helper.putPreference(OUTLET_PREF, outletJson)
    }

    override suspend fun getOutlet(): Outlet? {
        val outletJson = helper.getFirstPreference(OUTLET_PREF, "")
        return if (outletJson.isNotEmpty()) Json.decodeFromString(outletJson) else null
    }

    override suspend fun removeOutlet() {
        helper.removePreference(OUTLET_PREF)
    }

    override fun getOutletFlow(): Flow<Outlet?> {
        return helper.getPreference(OUTLET_PREF, "").map { outlet ->
            if (outlet.isNotEmpty()) Json.decodeFromString<Outlet>(outlet) else null
        }
    }

    companion object {
        val USER_TOKEN_PREF = stringPreferencesKey("USER_TOKEN_PREF")
        val AUTH_PREF = stringPreferencesKey("USER_PREF")
        val OUTLET_PREF = stringPreferencesKey("OUTLET_PREF")
    }
}
