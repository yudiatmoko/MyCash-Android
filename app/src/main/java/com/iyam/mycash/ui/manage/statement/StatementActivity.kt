package com.iyam.mycash.ui.manage.statement

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityStatementBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.ui.pointofsale.session.SessionActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.getDateBeforeDays
import com.iyam.mycash.utils.getDateBeforeDaysForTitle
import com.iyam.mycash.utils.getTodayDate
import com.iyam.mycash.utils.getTodayDateForTitle
import com.iyam.mycash.utils.proceedWhen
import com.iyam.mycash.utils.toCurrencyFormat
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatementActivity : AppCompatActivity() {

    private val binding: ActivityStatementBinding by lazy {
        ActivityStatementBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val topProductAdapter: TopProductAdapter by lazy {
        TopProductAdapter(
            context = this
        )
    }
    private var outletId: String? = null
    private val todayDate = getTodayDate()
    private val date7DaysAgo = getDateBeforeDays(7)
    private val date30DaysAgo = getDateBeforeDays(30)
    private val todayDateTitle = getTodayDateForTitle()
    private val date7DaysAgoTitle = getDateBeforeDaysForTitle(7)
    private val date30DaysAgoTitle = getDateBeforeDaysForTitle(30)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.statement)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        setupButtons()
        setupRecyclerView()
        setupSwipeRefresh()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.tvSessionTitle.setOnClickListener {
            navigateToSessionList()
        }
    }

    private fun navigateToSessionList() {
        val intent = Intent(this, SessionActivity::class.java)
        startActivity(intent)
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            observeData()
        }
    }

    private fun setupRecyclerView() {
        binding.rvTopProduct.apply {
            adapter = topProductAdapter
            layoutManager = LinearLayoutManager(this@StatementActivity)
        }
    }

    private fun observeData() {
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                binding.tvDate.text = todayDateTitle
                outletId = it.id
                when (binding.toggleGroup.checkedButtonId) {
                    R.id.btn_today -> {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = todayDate,
                            endDate = todayDate
                        )
                        binding.tvDate.text = todayDateTitle
                    }

                    R.id.btn_7_days -> {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date7DaysAgo,
                            endDate = todayDate
                        )
                        binding.tvDate.text =
                            getString(R.string.date_range, date7DaysAgoTitle, todayDateTitle)
                    }

                    R.id.btn_30_days -> {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date30DaysAgo,
                            endDate = todayDate
                        )
                        binding.tvDate.text =
                            getString(R.string.date_range, date30DaysAgoTitle, todayDateTitle)
                    }
                }
            }
        }
        posViewModel.sessionsRecapResult.observe(this) {
            binding.swipeRefresh.isRefreshing = false
            it.proceedWhen(
                doOnSuccess = {
                    binding.mainLayout.isVisible = true
                    binding.rvTopProduct.isVisible = true
                    binding.stateLayout.root.isVisible = false
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                    it.payload?.let {
                        topProductAdapter.setData(it.data.topProducts.filterNotNull())
                        binding.tvRevenue.text =
                            it.data.details?.totalRevenue?.toFloat()?.toCurrencyFormat()
                        binding.tvCash.text =
                            it.data.details?.paymentSummary?.cash?.toFloat()?.toCurrencyFormat()
                        binding.tvQris.text =
                            it.data.details?.paymentSummary?.qris?.toFloat()?.toCurrencyFormat()
                        binding.tvTotalTransaction.text =
                            it.data.details?.totalTransactions.toString()
                        binding.tvSuccessfulTransaction.text =
                            it.data.details?.successfulTransactions.toString()
                        binding.tvVoidTransaction.text =
                            it.data.details?.voidedTransactions.toString()
                        binding.tvSession.text = it.data.details?.totalSessions.toString()
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.rvTopProduct.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvTopProduct.isVisible = false
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

    private fun setupButtons() {
        binding.toggleGroup.check(R.id.btn_today)
        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleGroup.checkedButtonId) {
                R.id.btn_today -> {
                    if (isChecked) {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = todayDate,
                            endDate = todayDate
                        )
                        binding.tvDate.text = todayDateTitle
                    }
                }

                R.id.btn_7_days -> {
                    if (isChecked) {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date7DaysAgo,
                            endDate = todayDate
                        )
                        binding.tvDate.text =
                            getString(R.string.date_range, date7DaysAgoTitle, todayDateTitle)
                    }
                }

                R.id.btn_30_days -> {
                    if (isChecked) {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date30DaysAgo,
                            endDate = todayDate
                        )
                        binding.tvDate.text =
                            getString(R.string.date_range, date30DaysAgoTitle, todayDateTitle)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
