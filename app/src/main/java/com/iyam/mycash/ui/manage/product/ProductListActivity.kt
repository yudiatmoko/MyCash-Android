package com.iyam.mycash.ui.manage.product

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityProductListBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.manage.ManageViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProductListActivity : AppCompatActivity() {

    private val binding: ActivityProductListBinding by lazy {
        ActivityProductListBinding.inflate(
            layoutInflater,
            window.decorView as ViewGroup,
            false
        )
    }
    private var outletId: String? = null
    private val manageViewModel: ManageViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val productAdapter: ProductAdapter by lazy {
        ProductAdapter(
            context = this,
            deleteListener = {
                manageViewModel.doDeleteProduct(it.id.orEmpty())
            },
            editListener = {
                ProductUpdateActivity.startActivity(this, it)
            }
        )
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
        title = getString(R.string.product)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        setupRecyclerView()
        observeDeleteProduct()
        setSearchView()
        setOnRefreshListener()
    }

    private fun setOnRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            observeData()
        }
    }

    private fun setSearchView() {
        binding.svProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                manageViewModel.getProductsByOutlet(outletId.orEmpty(), name = query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    manageViewModel.getProductsByOutlet(outletId.orEmpty(), name = newText)
                }
                return false
            }
        })
    }

    private fun observeDeleteProduct() {
        manageViewModel.deleteProductResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(this, it.payload, Toast.LENGTH_SHORT).show()
                    observeData()
                },
                doOnError = {
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun observeData() {
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                manageViewModel.getProductsByOutlet(it.id.orEmpty())
                outletId = it.id
            }
        }
        manageViewModel.products.observe(this) {
            binding.swipeRefresh.isRefreshing = false
            it.proceedWhen(
                doOnSuccess = {
                    binding.mainLayout.isVisible = true
                    binding.rvProduct.isVisible = true
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
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvProduct.isVisible = false
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
    }

    private fun setupRecyclerView() {
        binding.rvProduct.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(this@ProductListActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_product) navigateToAddProduct()
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAddProduct() {
        val intent = Intent(this, AddProductActivity::class.java)
        startActivity(intent)
    }
}
