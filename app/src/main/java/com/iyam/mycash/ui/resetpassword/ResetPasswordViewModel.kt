package com.iyam.mycash.ui.resetpassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.resetpassword.ResetPasswordRequest
import com.iyam.mycash.data.repository.AuthRepository
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.launch

class ResetPasswordViewModel(
    private val repo: AuthRepository
) : ViewModel() {
    private val _generateOtp = MutableLiveData<ResultWrapper<String>>()
    val generateOtpResult: LiveData<ResultWrapper<String>>
        get() = _generateOtp

    fun doGenerateOtp(request: GenerateOtpRequest) {
        viewModelScope.launch {
            repo.generateOtp(request).collect {
                _generateOtp.value = it
            }
        }
    }

    private val _resetPassword = MutableLiveData<ResultWrapper<String>>()
    val resetPasswordResult: LiveData<ResultWrapper<String>>
        get() = _resetPassword

    fun doResetPassword(request: ResetPasswordRequest) {
        viewModelScope.launch {
            repo.resetPassword(request).collect {
                _resetPassword.value = it
            }
        }
    }
}
