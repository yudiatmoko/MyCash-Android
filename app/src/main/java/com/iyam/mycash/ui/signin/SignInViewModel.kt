package com.iyam.mycash.ui.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.user.login.LoginRequest
import com.iyam.mycash.data.repository.AuthRepository
import com.iyam.mycash.model.Auth
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.launch

class SignInViewModel(
    private val repo: AuthRepository
) : ViewModel() {
    private val _loginResult = MutableLiveData<ResultWrapper<Auth>>()
    val loginResult: LiveData<ResultWrapper<Auth>>
        get() = _loginResult

    fun doLogin(request: LoginRequest) {
        viewModelScope.launch {
            repo.login(request).collect {
                _loginResult.value = it
            }
        }
    }
}
