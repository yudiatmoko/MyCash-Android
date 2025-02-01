package com.iyam.mycash.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
import com.iyam.mycash.model.Auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val dataSource: UserPreferenceDataSource
) : ViewModel() {

    val userTokenLiveData = dataSource.getUserTokenFlow().asLiveData(Dispatchers.IO)

    fun setUserToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.setUserToken(token)
        }
    }

    suspend fun getUserToken(): String {
        return dataSource.getUserToken()
    }

    suspend fun removeUserToken() {
        return dataSource.removeUserToken()
    }

    val authLiveData = dataSource.getAuthFlow().asLiveData(Dispatchers.IO)

    fun setAuth(auth: Auth) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.setAuth(auth)
        }
    }

    suspend fun getAuth(): Auth? {
        return dataSource.getAuth()
    }

    suspend fun removeAuth() {
        return dataSource.removeAuth()
    }
}
