package com.iyam.mycash.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpRequest
import com.iyam.mycash.data.network.api.model.user.register.RegisterRequest
import com.iyam.mycash.data.repository.AuthRepository
import com.iyam.mycash.model.Auth
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repo: AuthRepository
) : ViewModel() {
    private val _registerResult = MutableLiveData<ResultWrapper<Auth>>()
    val registerResult: LiveData<ResultWrapper<Auth>>
        get() = _registerResult

    fun doRegister(request: RegisterRequest) {
        viewModelScope.launch {
            repo.register(request).collect {
                _registerResult.value = it
            }
        }
    }

    private val _generateOtp = MutableLiveData<ResultWrapper<String>>()
    val generateOtp: LiveData<ResultWrapper<String>>
        get() = _generateOtp

    fun doGenerateOtp(request: GenerateOtpRequest) {
        viewModelScope.launch {
            repo.generateOtp(request).collect {
                _generateOtp.value = it
            }
        }
    }

    private val _verifyOtp = MutableLiveData<ResultWrapper<String>>()
    val verifyOtp: LiveData<ResultWrapper<String>>
        get() = _verifyOtp

    fun doVerifyOtp(request: VerifyOtpRequest) {
        viewModelScope.launch {
            repo.verifyOtp(request).collect {
                _verifyOtp.value = it
            }
        }
    }
}
