package com.iyam.mycash.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.repository.AuthRepository
import com.iyam.mycash.data.repository.OutletRepository
import com.iyam.mycash.model.User
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val outletRepository: OutletRepository
) : ViewModel() {
    private val _getUserResult = MutableLiveData<ResultWrapper<User>>()

    val getUserResult: LiveData<ResultWrapper<User>>
        get() = _getUserResult

    fun getUserById(id: String) {
        viewModelScope.launch {
            authRepository.userById(id).collect {
                _getUserResult.value = it
            }
        }
    }

    private val _getUserUpdateResult = MutableLiveData<ResultWrapper<User>>()
    val getUserUpdateResult: LiveData<ResultWrapper<User>>
        get() = _getUserUpdateResult

    fun doUserUpdate(
        id: String,
        name: RequestBody,
        phoneNumber: RequestBody,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            authRepository.userUpdate(id, name, phoneNumber, image).collect {
                _getUserUpdateResult.value = it
            }
        }
    }
}
