package com.iyam.mycash.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.repository.AuthRepository
import com.iyam.mycash.model.User
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: AuthRepository) : ViewModel() {
    private val _getUserResult = MutableLiveData<ResultWrapper<User>>()

    val getUserResult: LiveData<ResultWrapper<User>>
        get() = _getUserResult

    fun getUserById(id: String) {
        viewModelScope.launch {
            repo.userById(id).collect {
                _getUserResult.value = it
            }
        }
    }
}
