package com.iyam.mycash.ui.pointofsale.session

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.session.SessionRequest
import com.iyam.mycash.databinding.ActivityAddSessionBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.editTextFormatPrice
import com.iyam.mycash.utils.getTodayDate
import com.iyam.mycash.utils.getTodayDateForTitle
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddSessionActivity : AppCompatActivity() {

    private val binding: ActivityAddSessionBinding by lazy {
        ActivityAddSessionBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val mainViewModel: MainViewModel by viewModel()
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val todayDate = getTodayDate()
    private val todayDateTitle = getTodayDateForTitle()
    private var outletId: String? = null
    private var userId: String? = null
    private var shift: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.add_new_session)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        setShiftDropdown()
        setOnClickListener()
        observeAddSession()
        startingCashEditTextListener()
    }

    private fun startingCashEditTextListener() {
        binding.layoutSessionForm.etStartingCash.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                binding.layoutSessionForm.etStartingCash.removeTextChangedListener(this)

                val originalString = editable.toString()
                val formattedString = editTextFormatPrice(originalString)

                binding.layoutSessionForm.etStartingCash.setText(formattedString)
                binding.layoutSessionForm.etStartingCash.setSelection(formattedString.length)

                binding.layoutSessionForm.etStartingCash.addTextChangedListener(this)
            }
        })
    }

    private fun observeAddSession() {
        posViewModel.addSessionResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.layoutSessionForm.loading.isVisible = false
                    binding.layoutSessionForm.btn.isVisible = true
                    binding.layoutSessionForm.btn.isEnabled = false
                    it.payload?.let {
                        mainViewModel.setSession(it)
                        Toast.makeText(this, "Add session successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    finish()
                },
                doOnLoading = {
                    binding.layoutSessionForm.loading.isVisible = true
                    binding.layoutSessionForm.btn.isVisible = false
                },
                doOnError = {
                    binding.layoutSessionForm.loading.isVisible = false
                    binding.layoutSessionForm.btn.isVisible = true
                    binding.layoutSessionForm.btn.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setOnClickListener() {
        binding.layoutSessionForm.btn.setOnClickListener {
            doAddSession()
        }
    }

    private fun doAddSession() {
        if (isFormValid()) {
            val sessionDate = todayDate
            val sessionShift = shift
            val sessionOutlet = outletId
            val sessionUser = userId
            val startingCash = binding.layoutSessionForm.etStartingCash.text.toString()
                .replace("[^\\d]".toRegex(), "")
            posViewModel.doAddSession(
                SessionRequest(
                    date = sessionDate,
                    outletId = sessionOutlet.orEmpty(),
                    shift = sessionShift.orEmpty(),
                    startingCash = startingCash.toDouble(),
                    userId = sessionUser.orEmpty()
                )
            )
        }
    }

    private fun isFormValid(): Boolean {
        val date = todayDate
        val startingCash = binding.layoutSessionForm.etStartingCash.text.toString().trim()
        return checkDateValidation(date) &&
            checkStartingCashValidation(startingCash)
    }

    private fun checkStartingCashValidation(startingCash: String): Boolean {
        return if (startingCash.isEmpty()) {
            binding.layoutSessionForm.tilStartingCash.isErrorEnabled = true
            binding.layoutSessionForm.tilStartingCash.error =
                getString(R.string.text_error_starting_cash_cannot_empty)
            false
        } else {
            binding.layoutSessionForm.tilStartingCash.isErrorEnabled = false
            true
        }
    }

    private fun checkDateValidation(date: String): Boolean {
        return if (date.isEmpty()) {
            binding.layoutSessionForm.tilSessionDate.isErrorEnabled = true
            binding.layoutSessionForm.tilSessionDate.error =
                getString(R.string.text_error_date_cannot_empty)
            false
        } else {
            binding.layoutSessionForm.tilSessionDate.isErrorEnabled = false
            true
        }
    }

    private fun setShiftDropdown() {
        val items = listOf(getString(R.string.morning), getString(R.string.evening))
        val adapter = ArrayAdapter(this, R.layout.layout_dropdown_item, items)
        binding.layoutSessionForm.etSessionDate.setText(todayDateTitle)
        binding.layoutSessionForm.etSessionDate.isEnabled = false
        binding.layoutSessionForm.etSessionShift.isEnabled = true
        binding.layoutSessionForm.etSessionShift.setAdapter(adapter)
        binding.layoutSessionForm.etSessionShift.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = items[position]
            shift = if (selectedItem == getString(R.string.morning)) {
                "MORNING"
            } else {
                "EVENING"
            }
        }
    }

    private fun observeData() {
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                outletId = it.id
            }
        }
        mainViewModel.authLiveData.observe(this) {
            it?.let {
                userId = it.id
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
