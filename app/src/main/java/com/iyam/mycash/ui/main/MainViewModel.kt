package com.iyam.mycash.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.local.datastore.UserPreferenceDataSource
import com.iyam.mycash.model.Auth
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.model.Session
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

    fun removeUserToken() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.removeUserToken()
        }
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

    fun removeAuth() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.removeAuth()
        }
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

    fun removeOutlet() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.removeOutlet()
        }
    }

    val sessionLiveData = dataSource.getSessionFlow().asLiveData(Dispatchers.IO)

    fun setSession(session: Session) {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.setSession(session)
        }
    }

    suspend fun getSession(): Session? {
        return dataSource.getSession()
    }

    fun removeSession() {
        viewModelScope.launch(Dispatchers.IO) {
            dataSource.removeSession()
        }
    }
}
