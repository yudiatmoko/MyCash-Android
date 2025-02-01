package com.iyam.mycash.data.local.datastore

import androidx.datastore.preferences.core.stringPreferencesKey
import com.iyam.mycash.model.Auth
import com.iyam.mycash.utils.PreferenceDataStoreHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
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

    companion object {
        val USER_TOKEN_PREF = stringPreferencesKey("USER_TOKEN_PREF")
        val AUTH_PREF = stringPreferencesKey("USER_PREF")
    }
}
