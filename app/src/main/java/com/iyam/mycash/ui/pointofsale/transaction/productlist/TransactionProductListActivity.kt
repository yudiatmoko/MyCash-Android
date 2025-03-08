package com.iyam.mycash.ui.pointofsale.transaction.productlist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityTransactionProductListBinding
import com.iyam.mycash.model.Category
import com.iyam.mycash.model.Product
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.manage.ManageViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.ui.pointofsale.transaction.cartlist.CartActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionProductListActivity : AppCompatActivity() {

    private val binding: ActivityTransactionProductListBinding by lazy {
        ActivityTransactionProductListBinding.inflate(
            layoutInflater,
            window.decorView as ViewGroup,
            false
        )
    }
    private var outletId: String? = null
    private var slug: String? = null
    private var nameQuery: String? = null
    private val mainViewModel: MainViewModel by viewModel()
    private val manageViewModel: ManageViewModel by viewModel()
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val productAdapter: TransactionProductAdapter by lazy {
        TransactionProductAdapter(this, ::onAddToCartClicked)
    }

    private fun onAddToCartClicked(product: Product, quantity: Int) {
        product.let {
            posViewModel.addToCart(it, quantity)
            val updatedList = productAdapter.getCurrentList().map {
                if (it.id == product.id) {
                    if (it.stock != null && it.stock > 0) {
                        it.copy(stock = it.stock - quantity)
                    } else {
                        it
                    }
                } else {
                    it
                }
            }
            productAdapter.setData(updatedList)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.add_new_transaction)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        observeAddToCart()
        setupRecyclerView()
        setCategoryDropdown()
        setSearchView()
        setOnRefreshListener()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnCartList.setOnClickListener {
            cartLauncher.launch(Intent(this, CartActivity::class.java))
        }
    }

    private fun observeAddToCart() {
        posViewModel.addToCartResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(
                        this,
                        "Product added to cart successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                doOnError = {
                    Toast.makeText(this, it.exception?.message.orEmpty(), Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setOnRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            posViewModel.getProductsByOutlet(
                outletId.orEmpty(),
                name = nameQuery,
                slug = slug,
                status = "true"
            )
        }
    }

    private fun setSearchView() {
        binding.svProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                posViewModel.getProductsByOutlet(
                    outletId.orEmpty(),
                    name = query,
                    slug = slug,
                    status = "true"
                )
                nameQuery = query
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    posViewModel.getProductsByOutlet(
                        outletId.orEmpty(),
                        name = newText,
                        slug = slug,
                        status = "true"
                    )
                    nameQuery = newText
                }
                return false
            }
        })
    }

    private fun setCategoryDropdown() {
        mainViewModel.outletLiveData.observe(this) { outlet ->
            outlet?.let {
                manageViewModel.getCategoriesByOutlet(it.id.orEmpty())
                outletId = it.id
            }
        }

        manageViewModel.categories.observe(this) { result ->
            result.proceedWhen(
                doOnSuccess = {
                    val categories = result.payload?.toMutableList() ?: mutableListOf()
                    categories.add(
                        0,
                        Category(
                            name = getString(R.string.all_categories),
                            slug = null,
                            id = null,
                            outletId = null,
                            createdAt = null,
                            updatedAt = null
                        )
                    )

                    val adapter = ArrayAdapter(
                        this,
                        R.layout.layout_dropdown_item,
                        categories.map { it.name }
                    )

                    binding.etProductCategory.apply {
                        isEnabled = true
                        setAdapter(adapter)
                        setOnItemClickListener { _, _, position, _ ->
                            val selectedSlug = categories[position].slug
                            slug = selectedSlug
                            posViewModel.getProductsByOutlet(
                                outletId.orEmpty(),
                                name = nameQuery,
                                slug = selectedSlug,
                                status = "true"
                            )
                        }

                        val lastCategoryIndex = categories.indexOfFirst { it.slug == slug }
                        if (lastCategoryIndex != -1) {
                            setText(categories[lastCategoryIndex].name, false)
                        }
                    }
                },
                doOnError = {
                    Toast.makeText(this, R.string.category_not_found, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun observeData() {
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                posViewModel.getProductsByOutlet(
                    it.id.orEmpty(),
                    name = nameQuery,
                    slug = slug,
                    status = "true"
                )
                outletId = it.id
            }
        }
        posViewModel.products.observe(this) {
            binding.swipeRefresh.isRefreshing = false
            it.proceedWhen(
                doOnSuccess = {
                    binding.mainLayout.isVisible = true
                    binding.rvProduct.isVisible = true
                    binding.cvContinue.isVisible = true
                    binding.stateLayout.root.isVisible = false
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                    it.payload?.let {
                        productAdapter.setData(it)
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.rvProduct.isVisible = false
                    binding.cvContinue.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvProduct.isVisible = false
                    binding.cvContinue.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = true
                    binding.stateLayout.animError.isVisible = true
                    binding.stateLayout.tvError.isVisible = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.unknown)
                    binding.stateLayout.tvError.text = errorMessage
                }
            )
        }
        posViewModel.cartList.observe(this) {
            binding.btnCartList.isVisible = it.payload?.first?.isNotEmpty() == true
        }
    }

    private fun setupRecyclerView() {
        binding.rvProduct.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@TransactionProductListActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private val cartLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                posViewModel.getProductsByOutlet(
                    outletId.orEmpty(),
                    name = nameQuery,
                    slug = slug,
                    status = "true"
                )
            }
        }
}
