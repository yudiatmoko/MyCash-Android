package com.iyam.mycash.ui.manage.product

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityAddProductBinding
import com.iyam.mycash.model.Product
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.manage.ManageViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import kotlin.math.roundToInt

class ProductUpdateActivity : AppCompatActivity() {

    private val binding: ActivityAddProductBinding by lazy {
        ActivityAddProductBinding.inflate(
            layoutInflater,
            window.decorView as ViewGroup,
            false
        )
    }
    private val manageViewModel: ManageViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private var productId: String? = null
    private var outletId: String? = null
    private var categoryId: String? = null
    private var status: Boolean? = null
    private var stockStatus: Boolean? = null
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.product)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeIntent()
        setupForm()
        setCategoryDropdown()
        observeUpdateProduct()
        setOnClickListener()
    }

    private fun observeUpdateProduct() {
        manageViewModel.updateProductResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.layoutProductForm.loading.isVisible = false
                    binding.layoutProductForm.btn.isVisible = true
                    binding.layoutProductForm.btn.isEnabled = false
                    it.payload?.let {
                        Toast.makeText(this, "Product updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    finish()
                },
                doOnLoading = {
                    binding.layoutProductForm.loading.isVisible = true
                    binding.layoutProductForm.btn.isVisible = false
                },
                doOnError = {
                    binding.layoutProductForm.loading.isVisible = false
                    binding.layoutProductForm.btn.isVisible = true
                    binding.layoutProductForm.btn.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setOnClickListener() {
        binding.layoutProductForm.btnAddImage.setOnClickListener {
            imagePicker()
        }
        binding.layoutProductForm.btn.setOnClickListener {
            doUpdateProduct()
        }
    }

    private fun isFormValid(): Boolean {
        val productName = binding.layoutProductForm.etProductName.text.toString().trim()
        val productDescription =
            binding.layoutProductForm.etProductDescription.text.toString().trim()
        val productPrice = binding.layoutProductForm.etProductPrice.text.toString().trim()
        val productStock = binding.layoutProductForm.etProductStock.text.toString().trim()
        val productStockStatus = stockStatus

        return checkProductNameValidation(productName) &&
            checkProductDescriptionValidation(productDescription) &&
            checkProductPriceValidation(productPrice) &&
            checkProductStockValidation(productStock, productStockStatus)
    }

    private fun checkProductStockValidation(
        productStock: String,
        status: Boolean? = null
    ): Boolean {
        return if (status == true && productStock.isEmpty() || status == true && productStock == "0") {
            binding.layoutProductForm.tilProductStock.isErrorEnabled = true
            binding.layoutProductForm.tilProductStock.error =
                getString(R.string.text_error_stock_cannot_empty)
            false
        } else {
            binding.layoutProductForm.tilProductStock.isErrorEnabled = false
            true
        }
    }

    private fun checkProductPriceValidation(productPrice: String): Boolean {
        return if (productPrice.isEmpty()) {
            binding.layoutProductForm.tilProductPrice.isErrorEnabled = true
            binding.layoutProductForm.tilProductPrice.error =
                getString(R.string.text_error_price_cannot_empty)
            false
        } else {
            binding.layoutProductForm.tilProductPrice.isErrorEnabled = false
            true
        }
    }

    private fun checkProductDescriptionValidation(productDescription: String): Boolean {
        return if (productDescription.isEmpty()) {
            binding.layoutProductForm.tilProductDescription.isErrorEnabled = true
            binding.layoutProductForm.tilProductDescription.error =
                getString(R.string.text_error_description_cannot_empty)
            false
        } else {
            binding.layoutProductForm.tilProductDescription.isErrorEnabled = false
            true
        }
    }

    private fun checkProductNameValidation(productName: String): Boolean {
        return if (productName.isEmpty()) {
            binding.layoutProductForm.tilProductName.isErrorEnabled = true
            binding.layoutProductForm.tilProductName.error =
                getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.layoutProductForm.tilProductName.isErrorEnabled = false
            true
        }
    }

    private fun doUpdateProduct() {
        if (isFormValid()) {
            val productName = binding.layoutProductForm.etProductName.text.toString().trim()
            val productDescription =
                binding.layoutProductForm.etProductDescription.text.toString().trim()
            val productPrice = binding.layoutProductForm.etProductPrice.text.toString().trim()
            val productStock = binding.layoutProductForm.etProductStock.text.toString().trim()
            val productStatus = status
            val productCategory = categoryId
            val productOutlet = outletId
            val requestFile = getFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part? =
                requestFile?.let {
                    MultipartBody.Part.createFormData(
                        "image",
                        getFile?.name ?: "",
                        it
                    )
                }

            val name = productName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val description =
                productDescription.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val price = productPrice.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val stock =
                if (stockStatus == false) {
                    null.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
                } else {
                    productStock.toRequestBody("multipart/form-data".toMediaTypeOrNull())
                }
            val status =
                productStatus.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val category =
                productCategory.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val outlet =
                productOutlet.toString().toRequestBody("multipart/form-data".toMediaTypeOrNull())

            manageViewModel.doUpdateProduct(
                id = productId.orEmpty(),
                name = name,
                description = description,
                price = price,
                status = status,
                stock = stock,
                categoryId = category,
                outletId = outlet,
                image = imageMultipart
            )
        }
    }

    private fun imagePicker() {
        ImagePicker.with(this)
            .cropSquare()
            .galleryOnly()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            val img = uri?.toFile()
            binding.layoutProductForm.ivProductPhoto.load(uri)
            getFile = img
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupForm() {
        binding.layoutProductForm.switchStock.setOnCheckedChangeListener { _, isChecked ->
            stockStatus = isChecked
            if (isChecked) {
                binding.layoutProductForm.etProductStock.isEnabled = true
                binding.layoutProductForm.etProductStock.hint =
                    getString(R.string.product_stock_example)
            } else {
                binding.layoutProductForm.etProductStock.isEnabled = false
                binding.layoutProductForm.etProductStock.hint = getString(R.string.unlimited)
                binding.layoutProductForm.etProductStock.text?.clear()
            }
        }
        binding.layoutProductForm.switchStatus.setOnCheckedChangeListener { _, isChecked ->
            status = isChecked
        }
    }

    private fun setCategoryDropdown() {
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                manageViewModel.getCategoriesByOutlet(it.id.orEmpty())
                outletId = it.id
            }
        }
        manageViewModel.categories.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    val categories = it.payload
                    val adapter = categories?.let { category ->
                        ArrayAdapter(
                            this,
                            R.layout.layout_dropdown_item,
                            category.map { it.name }
                        )
                    }
                    binding.layoutProductForm.etProductCategory.isEnabled = true
                    binding.layoutProductForm.etProductCategory.setAdapter(adapter)
                    binding.layoutProductForm.etProductCategory.setOnItemClickListener { _, _, position, _ ->
                        val selectedCategory =
                            categories?.get(position)
                        categoryId = selectedCategory?.id
                    }
                }
            )
        }
    }

    private fun observeIntent() {
        val product = intent.getParcelableExtra<Product>(EXTRA_PRODUCT)
        product?.let {
            val stock = product.stock
            val categoryId = product.categoryId
            this.categoryId = product.categoryId
            outletId = product.outletId
            productId = product.id
            status = product.status
            manageViewModel.getCategoryById(categoryId.orEmpty())
            manageViewModel.category.observe(this) {
                it.proceedWhen(
                    doOnSuccess = {
                        binding.layoutProductForm.etProductCategory.setText(it.payload?.name)
                        binding.layoutProductForm.etProductCategory.isEnabled = true
                    }
                )
            }
            if (stock != null && stock > 0) {
                stockStatus = true
                binding.layoutProductForm.etProductStock.setText(stock.toString())
                binding.layoutProductForm.etProductStock.isEnabled = true
                binding.layoutProductForm.switchStock.isChecked = true
            } else {
                stockStatus = false
                binding.layoutProductForm.etProductStock.hint = getString(R.string.unlimited)
                binding.layoutProductForm.etProductStock.isEnabled = false
                binding.layoutProductForm.switchStock.isChecked = false
            }
            if (product.status == true) {
                binding.layoutProductForm.switchStatus.isChecked = true
            } else {
                binding.layoutProductForm.switchStatus.isChecked = false
            }
            binding.layoutProductForm.etProductName.setText(product.name)
            binding.layoutProductForm.etProductDescription.setText(product.description)
            binding.layoutProductForm.etProductPrice.setText(product.price?.roundToInt().toString())
            if (product.image.isNullOrBlank()) {
                binding.layoutProductForm.ivProductPhoto.load(R.drawable.img_placeholder_general)
            } else {
                binding.layoutProductForm.ivProductPhoto.load(product.image)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private val EXTRA_PRODUCT = "EXTRA_PRODUCT"
        fun startActivity(context: Context, product: Product) {
            val intent = Intent(context, ProductUpdateActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT, product)
            context.startActivity(intent)
        }
    }
}
