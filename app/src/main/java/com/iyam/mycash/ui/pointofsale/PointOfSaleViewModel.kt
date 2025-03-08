package com.iyam.mycash.ui.pointofsale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.recap.RecapResponse
import com.iyam.mycash.data.network.api.model.session.RecapSessionRequest
import com.iyam.mycash.data.network.api.model.session.SessionRequest
import com.iyam.mycash.data.network.api.model.transaction.create.TransactionRequest
import com.iyam.mycash.data.repository.CartRepository
import com.iyam.mycash.data.repository.SessionRepository
import com.iyam.mycash.data.repository.TransactionRepository
import com.iyam.mycash.model.Cart
import com.iyam.mycash.model.Product
import com.iyam.mycash.model.Session
import com.iyam.mycash.model.Transaction
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class PointOfSaleViewModel(
    private val sessionRepo: SessionRepository,
    private val cartRepository: CartRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

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

    private val _uploadTransactionImageResult = MutableLiveData<ResultWrapper<String>>()

    val uploadTransactionImageResult: LiveData<ResultWrapper<String>>
        get() = _uploadTransactionImageResult

    fun uploadTransactionImage(
        transactionId: String,
        image: MultipartBody.Part
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.uploadTransactionImage(transactionId, image).collect {
                _uploadTransactionImageResult.postValue(it)
            }
        }
    }

    private val _products = MutableLiveData<ResultWrapper<List<Product>>>()

    val products: LiveData<ResultWrapper<List<Product>>>
        get() = _products

    fun getProductsByOutlet(
        outletId: String,
        name: String? = null,
        slug: String? = null,
        status: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.getProductWithUpdatedStock(outletId, name, slug, status).collect {
                _products.postValue(it)
            }
        }
    }

    private val _addToCartResult = MutableLiveData<ResultWrapper<Boolean>>()

    val addToCartResult: LiveData<ResultWrapper<Boolean>>
        get() = _addToCartResult

    fun addToCart(product: Product, qty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            cartRepository.createCart(product, qty).collect { result ->
                _addToCartResult.postValue(result)
            }
        }
    }

    val cartList = cartRepository.getCartList().asLiveData(Dispatchers.IO)

    fun decreaseCart(item: Cart) {
        viewModelScope.launch(Dispatchers.IO) { cartRepository.decreaseCart(item).collect() }
    }

    fun increaseCart(item: Cart) {
        viewModelScope.launch(Dispatchers.IO) { cartRepository.increaseCart(item).collect() }
    }

    fun deleteCart(item: Cart) {
        viewModelScope.launch(Dispatchers.IO) { cartRepository.deleteCart(item).collect() }
    }

    fun updateQuantity(item: Cart) {
        viewModelScope.launch(Dispatchers.IO) { cartRepository.setCartQuantity(item).collect() }
    }

    fun deleteAllCart() {
        viewModelScope.launch(Dispatchers.IO) { cartRepository.deleteAll().collect() }
    }

    private val _addTransactionResult = MutableLiveData<ResultWrapper<String>>()

    val addTransactionResult: LiveData<ResultWrapper<String>>
        get() = _addTransactionResult

    fun doAddTransaction(request: TransactionRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.addTransaction(request).collect {
                _addTransactionResult.postValue(it)
            }
        }
    }

    private val _transactionByIdResult = MutableLiveData<ResultWrapper<Transaction>>()

    val transactionByIdResult: LiveData<ResultWrapper<Transaction>>
        get() = _transactionByIdResult

    fun getTransactionById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.transactionById(id).collect {
                _transactionByIdResult.postValue(it)
            }
        }
    }

    private val _transactionsBySessionResult = MutableLiveData<ResultWrapper<List<Transaction>>>()

    val transactionsBySessionResult: LiveData<ResultWrapper<List<Transaction>>>
        get() = _transactionsBySessionResult

    fun getTransactionsBySession(sessionId: String, number: String? = null, order: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.transactionList(sessionId, number, order).collect {
                _transactionsBySessionResult.postValue(it)
            }
        }
    }

    private val _voidTransactionResult = MutableLiveData<ResultWrapper<String>>()

    val voidTransactionResult: LiveData<ResultWrapper<String>>
        get() = _voidTransactionResult

    fun voidTransaction(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.voidTransaction(id).collect {
                _voidTransactionResult.postValue(it)
            }
        }
    }
}
