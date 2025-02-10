package com.iyam.mycash.data.network.api.service

import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val userPref: UserPreferenceDataSource
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val authToken = runBlocking {
            userPref.getUserTokenFlow().first()
        }

        val request = chain.request().newBuilder().apply {
            if (authToken.isNotEmpty()) {
                header("Authorization", "Bearer $authToken")
            }
        }.build()

        val response = chain.proceed(request)

        if (response.code == 403) {
            runBlocking {
                userPref.removeUserToken()
                userPref.removeAuth()
            }
        }

        return response
    }
}
