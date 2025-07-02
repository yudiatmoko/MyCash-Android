package com.iyam.mycash.ui.pointofsale.transaction

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityTransactionBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.ui.pointofsale.transaction.detail.TransactionDetailActivity
import com.iyam.mycash.ui.pointofsale.transaction.productlist.TransactionProductListActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class TransactionActivity : AppCompatActivity() {

    private val binding: ActivityTransactionBinding by lazy {
        ActivityTransactionBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val mainViewModel: MainViewModel by viewModel()
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val transactionAdapter: TransactionAdapter by lazy {
        TransactionAdapter(
            context = this,
            onClick = {
                TransactionDetailActivity.startActivity(this@TransactionActivity, it, false)
            }
        )
    }
    private var sessionId: String? = null
    private var sorting: String? = "desc"
    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.transaction_list)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        setupRecyclerView()
        setupSorting()
        setupSearchView()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            observeData()
        }
    }

    private fun setupSearchView() {
        binding.svTransaction.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handleSearch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    posViewModel.getTransactionsBySession(
                        sessionId.orEmpty(),
                        number = null,
                        id = null,
                        order = sorting
                    )
                } else {
                    handleSearch(newText)
                }
                return false
            }

            private fun handleSearch(query: String?) {
                if (query.isNullOrBlank()) return

                number = null
                val isNumeric = query.toIntOrNull() != null

                if (isNumeric) {
                    number = query
                    posViewModel.getTransactionsBySession(
                        sessionId.orEmpty(),
                        number = query,
                        id = null,
                        order = sorting
                    )
                } else {
                    posViewModel.getTransactionsBySession(
                        sessionId.orEmpty(),
                        number = null,
                        id = query,
                        order = sorting
                    )
                }
            }
        })
    }

    private fun setupSorting() {
        val items = listOf(getString(R.string.newest), getString(R.string.oldest))
        val adapter = ArrayAdapter(this, R.layout.layout_dropdown_item, items)
        binding.etSorting.isEnabled = true
        binding.etSorting.setAdapter(adapter)
        binding.etSorting.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            sorting = if (selectedItem == getString(R.string.newest)) "desc" else "asc"
            posViewModel.getTransactionsBySession(sessionId.orEmpty(), number, sorting)
        }
    }

    private fun setupRecyclerView() {
        binding.rvTransaction.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(this@TransactionActivity)
        }
    }

    private fun observeData() {
        mainViewModel.sessionLiveData.observe(this) {
            it?.let {
                sessionId = it.id
                posViewModel.getTransactionsBySession(it.id.orEmpty(), order = sorting, number = number)
            }
        }
        posViewModel.transactionsBySessionResult.observe(this) {
            binding.swipeRefresh.isRefreshing = false
            it.proceedWhen(
                doOnSuccess = {
                    binding.mainLayout.isVisible = true
                    binding.rvTransaction.isVisible = true
                    binding.stateLayout.root.isVisible = false
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                    it.payload?.let {
                        transactionAdapter.setData(it)
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.rvTransaction.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvTransaction.isVisible = false
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (sessionId == null) return false
        menuInflater.inflate(R.menu.add_transaction_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_transaction) navigateToAddTransaction()
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAddTransaction() {
        val intent = Intent(this, TransactionProductListActivity::class.java)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
