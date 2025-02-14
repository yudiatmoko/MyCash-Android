package com.iyam.mycash.ui.pointofsale.session

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivitySessionBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.getDateBeforeDays
import com.iyam.mycash.utils.getDateBeforeDaysForTitle
import com.iyam.mycash.utils.getTodayDate
import com.iyam.mycash.utils.getTodayDateForTitle
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SessionActivity : AppCompatActivity() {

    private val binding: ActivitySessionBinding by lazy {
        ActivitySessionBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val sessionAdapter: SessionAdapter by lazy {
        SessionAdapter(
            context = this,
            onClick = {
                SessionDetailActivity.startActivity(this@SessionActivity, it)
            }
        )
    }
    private var outletId: String? = null
    private var sessionId: String? = null
    private var sorting: String? = "desc"
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
        title = getString(R.string.session)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        setupRecyclerView()
        setupSorting()
        setupButtons()
        setupSwipeRefresh()
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            val checkedId = binding.toggleGroup.checkedButtonId

            when (checkedId) {
                R.id.btn_today -> {
                    posViewModel.getSessionsRecapByOutlet(
                        outletId.orEmpty(),
                        startDate = todayDate,
                        endDate = todayDate,
                        order = sorting
                    )
                }

                R.id.btn_7_days -> {
                    posViewModel.getSessionsRecapByOutlet(
                        outletId.orEmpty(),
                        startDate = date7DaysAgo,
                        endDate = todayDate,
                        order = sorting
                    )
                }

                R.id.btn_30_days -> {
                    posViewModel.getSessionsRecapByOutlet(
                        outletId.orEmpty(),
                        startDate = date30DaysAgo,
                        endDate = todayDate,
                        order = sorting
                    )
                }
            }

            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupSorting() {
        val items = listOf(getString(R.string.newest), getString(R.string.oldest))
        val adapter = ArrayAdapter(this, R.layout.layout_dropdown_item, items)
        binding.etSorting.isEnabled = true
        binding.etSorting.setAdapter(adapter)
        binding.etSorting.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            sorting = if (selectedItem == getString(R.string.newest)) "desc" else "asc"

            val checkedId = binding.toggleGroup.checkedButtonId
            when (checkedId) {
                R.id.btn_today -> {
                    posViewModel.getSessionsRecapByOutlet(
                        outletId.orEmpty(),
                        startDate = todayDate,
                        endDate = todayDate,
                        order = sorting
                    )
                }

                R.id.btn_7_days -> {
                    posViewModel.getSessionsRecapByOutlet(
                        outletId.orEmpty(),
                        startDate = date7DaysAgo,
                        endDate = todayDate,
                        order = sorting
                    )
                }

                R.id.btn_30_days -> {
                    posViewModel.getSessionsRecapByOutlet(
                        outletId.orEmpty(),
                        startDate = date30DaysAgo,
                        endDate = todayDate,
                        order = sorting
                    )
                }
            }
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
                            endDate = todayDate,
                            order = sorting
                        )
                        binding.tvDate.text = todayDateTitle
                    }
                }

                R.id.btn_7_days -> {
                    if (isChecked) {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date7DaysAgo,
                            endDate = todayDate,
                            order = sorting
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
                            endDate = todayDate,
                            order = sorting
                        )
                        binding.tvDate.text =
                            getString(R.string.date_range, date30DaysAgoTitle, todayDateTitle)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvSession.apply {
            adapter = sessionAdapter
            layoutManager = LinearLayoutManager(this@SessionActivity)
        }
    }

    private fun observeData() {
        mainViewModel.sessionLiveData.observe(this) {
            it?.let {
                sessionId = it.id
            }
        }
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                binding.tvDate.text = todayDateTitle
                outletId = it.id
                when (binding.toggleGroup.checkedButtonId) {
                    R.id.btn_today -> {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = todayDate,
                            endDate = todayDate,
                            order = sorting
                        )
                        binding.tvDate.text = todayDateTitle
                    }

                    R.id.btn_7_days -> {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date7DaysAgo,
                            endDate = todayDate,
                            order = sorting
                        )
                        binding.tvDate.text =
                            getString(R.string.date_range, date7DaysAgoTitle, todayDateTitle)
                    }

                    R.id.btn_30_days -> {
                        posViewModel.getSessionsRecapByOutlet(
                            outletId.orEmpty(),
                            startDate = date30DaysAgo,
                            endDate = todayDate,
                            order = sorting
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
                    binding.rvSession.isVisible = true
                    binding.stateLayout.root.isVisible = false
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                    it.payload?.let {
                        sessionAdapter.setData(it.data.sessions.filterNotNull())
                        if (it.data.sessions.isEmpty()) {
                            binding.mainLayout.isVisible = true
                            binding.rvSession.isVisible = false
                            binding.stateLayout.root.isVisible = true
                            binding.stateLayout.animLoading.isVisible = false
                            binding.stateLayout.llAnimError.isVisible = true
                            binding.stateLayout.tvError.isVisible = true
                            binding.stateLayout.tvError.text = getString(R.string.session_not_found)
                        }
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.rvSession.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvSession.isVisible = false
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
        menuInflater.inflate(R.menu.add_session_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_session) navigateToAddSession()
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToAddSession() {
        val intent = Intent(this, AddSessionActivity::class.java)
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
