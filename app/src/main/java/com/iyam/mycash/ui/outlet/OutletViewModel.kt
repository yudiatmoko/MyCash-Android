package com.iyam.mycash.ui.outlet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.outlet.OutletRequest
import com.iyam.mycash.data.repository.OutletRepository
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class OutletViewModel(
    private val repo: OutletRepository
) : ViewModel() {
    private val _outlets = MutableLiveData<ResultWrapper<List<Outlet>>>()

    val outlets: LiveData<ResultWrapper<List<Outlet>>>
        get() = _outlets

    fun getOutletsByUser(name: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.outletsByUser(name).collect {
                _outlets.postValue(it)
            }
        }
    }

    private val _outlet = MutableLiveData<ResultWrapper<Outlet>>()

    val outlet: LiveData<ResultWrapper<Outlet>>
        get() = _outlet

    fun getOutletById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.outletById(id).collect {
                _outlet.postValue(it)
            }
        }
    }

    private val _addOutletResult = MutableLiveData<ResultWrapper<Outlet>>()

    val addOutletResult: LiveData<ResultWrapper<Outlet>>
        get() = _addOutletResult

    fun doAddOutlet(request: OutletRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addOutlet(request).collect {
                _addOutletResult.postValue(it)
            }
        }
    }

    private val _updateOutletResult = MutableLiveData<ResultWrapper<Outlet>>()

    val updateOutletResult: LiveData<ResultWrapper<Outlet>>
        get() = _updateOutletResult

    fun doUpdateOutlet(
        id: String,
        name: RequestBody,
        type: RequestBody,
        phoneNumber: RequestBody,
        address: RequestBody,
        district: RequestBody,
        city: RequestBody,
        province: RequestBody,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateOutlet(id, name, type, phoneNumber, address, district, city, province, image)
                .collect {
                    _updateOutletResult.postValue(it)
                }
        }
    }
}
