package com.iyam.mycash.ui.pointofsale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.recap.RecapResponse
import com.iyam.mycash.data.network.api.model.session.RecapSessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionRequest
import com.iyam.mycash.data.repository.SessionRepository
import com.iyam.mycash.model.Session
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class PointOfSaleViewModel(
    private val sessionRepo: SessionRepository
) : ViewModel() {

    private val _sessions = MutableLiveData<ResultWrapper<List<Session>>>()

    val sessions: LiveData<ResultWrapper<List<Session>>>
        get() = _sessions

    fun getSessionsByOutlet(outletId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.sessionsByOutlet(outletId).collect {
                _sessions.postValue(it)
            }
        }
    }

    private val _sessionById = MutableLiveData<ResultWrapper<Session>>()

    val sessionById: LiveData<ResultWrapper<Session>>
        get() = _sessionById

    fun getSessionById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.sessionById(id).collect {
                _sessionById.postValue(it)
            }
        }
    }

    private val _addSessionResult = MutableLiveData<ResultWrapper<Session>>()

    val addSessionResult: LiveData<ResultWrapper<Session>>
        get() = _addSessionResult

    fun doAddSession(request: SessionRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.addSession(request).collect {
                _addSessionResult.postValue(it)
            }
        }
    }

    private val _updateSessionResult = MutableLiveData<ResultWrapper<Session>>()

    val updateSessionResult: LiveData<ResultWrapper<Session>>
        get() = _updateSessionResult

    fun doUpdateSession(id: String, request: RecapSessionRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.updateSession(id, request).collect {
                _updateSessionResult.postValue(it)
            }
        }
    }

    private val _deleteSessionResult = MutableLiveData<ResultWrapper<String>>()

    val deleteSessionResult: LiveData<ResultWrapper<String>>
        get() = _deleteSessionResult

    fun doDeleteSession(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.deleteSession(id).collect {
                _deleteSessionResult.postValue(it)
            }
        }
    }

    private val _sessionsRecapResult = MutableLiveData<ResultWrapper<RecapResponse>>()

    val sessionsRecapResult: LiveData<ResultWrapper<RecapResponse>>
        get() = _sessionsRecapResult

    fun getSessionsRecapByOutlet(
        outletId: String,
        startDate: String? = null,
        endDate: String? = null,
        order: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.recapByOutlet(outletId, startDate, endDate, order).collect {
                _sessionsRecapResult.postValue(it)
            }
        }
    }

    private val _uploadSessionImageResult = MutableLiveData<ResultWrapper<String>>()

    val uploadSessionImageResult: LiveData<ResultWrapper<String>>
        get() = _uploadSessionImageResult

    fun uploadSessionImage(
        sessionId: String,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            sessionRepo.uploadSessionImage(sessionId, image).collect {
                _uploadSessionImageResult.postValue(it)
            }
        }
    }
}
