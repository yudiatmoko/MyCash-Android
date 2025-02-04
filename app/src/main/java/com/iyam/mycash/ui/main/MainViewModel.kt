package com.iyam.mycash.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
import com.iyam.mycash.model.Auth
import com.iyam.mycash.model.Outlet
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

    val outletLiveData = dataSource.getOutletFlow().asLiveData(Dispatchers.IO)

    fun setOutlet(outlet: Outlet) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.setOutlet(outlet)
        }
    }

    suspend fun getOutlet(): Outlet? {
        return dataSource.getOutlet()
    }

    suspend fun removeOutlet() {
        return dataSource.removeOutlet()
    }
}
