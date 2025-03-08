package com.iyam.mycash.ui.manage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iyam.mycash.data.network.api.model.category.CategoryRequest
import com.iyam.mycash.data.repository.CategoryRepository
import com.iyam.mycash.data.repository.ProductRepository
import com.iyam.mycash.model.Category
import com.iyam.mycash.model.Product
import com.iyam.mycash.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ManageViewModel(
    private val categoryRepo: CategoryRepository,
    private val productRepo: ProductRepository
) : ViewModel() {
    private val _categories = MutableLiveData<ResultWrapper<List<Category>>>()

    val categories: LiveData<ResultWrapper<List<Category>>>
        get() = _categories

    fun getCategoriesByOutlet(outletId: String, name: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.categoriesByOutlet(outletId, name).collect {
                _categories.postValue(it)
            }
        }
    }

    private val _category = MutableLiveData<ResultWrapper<Category>>()

    val category: LiveData<ResultWrapper<Category>>
        get() = _category

    fun getCategoryById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.categoryById(id).collect {
                _category.postValue(it)
            }
        }
    }

    private val _addCategoryResult = MutableLiveData<ResultWrapper<Category>>()

    val addCategoryResult: LiveData<ResultWrapper<Category>>
        get() = _addCategoryResult

    fun doAddCategory(request: CategoryRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.addCategory(request).collect {
                _addCategoryResult.postValue(it)
            }
        }
    }

    private val _deleteCategoryResult = MutableLiveData<ResultWrapper<String>>()
    val deleteCategoryResult: LiveData<ResultWrapper<String>>
        get() = _deleteCategoryResult

    fun doDeleteCategory(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            categoryRepo.deleteCategory(id).collect {
                _deleteCategoryResult.postValue(it)
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
            productRepo.productsByOutlet(outletId, name, slug, status).collect {
                _products.postValue(it)
            }
        }
    }

    private val _product = MutableLiveData<ResultWrapper<Product>>()

    val product: LiveData<ResultWrapper<Product>>
        get() = _product

    fun getProductById(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.productById(id).collect {
                _product.postValue(it)
            }
        }
    }

    private val _addProductResult = MutableLiveData<ResultWrapper<Product>>()

    val addProductResult: LiveData<ResultWrapper<Product>>
        get() = _addProductResult

    fun doAddProduct(
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.addProduct(
                name,
                description,
                price,
                status,
                stock,
                categoryId,
                outletId,
                image
            ).collect {
                _addProductResult.postValue(it)
            }
        }
    }

    private val _updateProductResult = MutableLiveData<ResultWrapper<Product>>()

    val updateProductResult: LiveData<ResultWrapper<Product>>
        get() = _updateProductResult

    fun doUpdateProduct(
        id: String,
        name: RequestBody,
        description: RequestBody,
        price: RequestBody,
        status: RequestBody,
        stock: RequestBody?,
        categoryId: RequestBody,
        outletId: RequestBody,
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.updateProduct(
                id,
                name,
                description,
                price,
                status,
                stock,
                categoryId,
                outletId,
                image
            ).collect {
                _updateProductResult.postValue(it)
            }
        }
    }

    private val _deleteProductResult = MutableLiveData<ResultWrapper<String>>()
    val deleteProductResult: LiveData<ResultWrapper<String>>
        get() = _deleteProductResult

    fun doDeleteProduct(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            productRepo.deleteProduct(id).collect {
                _deleteProductResult.postValue(it)
            }
        }
    }
}
