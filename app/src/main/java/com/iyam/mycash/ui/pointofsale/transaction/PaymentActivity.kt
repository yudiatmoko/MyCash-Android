package com.iyam.mycash.ui.pointofsale.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.transaction.create.TransactionRequest
import com.iyam.mycash.data.network.api.model.transaction.create.toDetailTransactionRequestList
import com.iyam.mycash.databinding.ActivityPaymentBinding
import com.iyam.mycash.model.Cart
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.ui.pointofsale.transaction.detail.TransactionDetailActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.SpacingItemDecoration
import com.iyam.mycash.utils.proceedWhen
import com.iyam.mycash.utils.toCurrencyFormat
import org.koin.androidx.viewmodel.ext.android.viewModel

class PaymentActivity : AppCompatActivity() {

    private val binding: ActivityPaymentBinding by lazy {
        ActivityPaymentBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val denominationAdapter: DenominationAdapter by lazy {
        DenominationAdapter(::onDenominationClicked)
    }
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private var paymentAmount: Double = 0.0
    private var totalPrice: Double = 0.0
    private var cartList: List<Cart> = emptyList()
    private var paymentMethod: String = ""
    private var sessionId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.payment)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        totalPrice = intent.getDoubleExtra(EXTRA_TOTAL_BILL, 0.0)
        binding.tvTotalPrice.text = totalPrice.toFloat().toCurrencyFormat()
        observeData()
        setupRecyclerView()
        setupButtonListener()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btn.setOnClickListener {
            doAddTransaction()
        }
    }

    private fun doAddTransaction() {
        if (isFormValid()) {
            posViewModel.doAddTransaction(
                TransactionRequest(
                    sessionId = sessionId,
                    paymentMethod = paymentMethod,
                    totalPayment = paymentAmount,
                    note = binding.formPayment.etNote.text.toString().trim(),
                    detail = cartList.toDetailTransactionRequestList()
                )
            )
        }
    }

    private fun isFormValid(): Boolean {
        return checkSessionId() && checkPaymentMethod() && checkDetail()
    }

    private fun checkDetail(): Boolean {
        if (cartList.isEmpty()) {
            Toast.makeText(this, R.string.cart_is_empty, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPaymentMethod(): Boolean {
        if (paymentMethod.isEmpty()) {
            Toast.makeText(this, R.string.payment_method_not_selected, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkSessionId(): Boolean {
        if (sessionId.isEmpty()) {
            Toast.makeText(this, R.string.session_not_found, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun observeData() {
        posViewModel.cartList.observe(this) {
            cartList = it.payload?.first ?: emptyList()
        }
        mainViewModel.sessionLiveData.observe(this) {
            sessionId = it?.id.orEmpty()
        }
        posViewModel.addTransactionResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    binding.btn.isEnabled = false
                    posViewModel.deleteAllCart()
                    posViewModel.getTransactionById(it.payload.orEmpty())
                    navigateToDetailTransaction()
                },
                doOnLoading = {
                    binding.loading.isVisible = true
                    binding.btn.isVisible = false
                },
                doOnError = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    binding.btn.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToDetailTransaction() {
        posViewModel.transactionByIdResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    it.payload?.let { transaction ->
                        TransactionDetailActivity.startActivity(
                            this,
                            transaction
                        )
                    }
                    finish()
                }
            )
        }
    }

    private fun setupButtonListener() {
        binding.formPayment.etTotalPayment.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    binding.formPayment.etTotalPayment.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: 0.0
                    val formatted = parsed.toFloat().toCurrencyFormat()

                    current = formatted
                    binding.formPayment.etTotalPayment.setText(formatted)
                    binding.formPayment.etTotalPayment.setSelection(formatted.length)

                    paymentAmount = parsed
                    calculateChange()

                    binding.formPayment.etTotalPayment.addTextChangedListener(this)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.toggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (binding.toggleGroup.checkedButtonId) {
                R.id.btn_cash -> {
                    if (isChecked) {
                        paymentMethod = "CASH"
                    }
                }

                R.id.btn_qris -> {
                    if (isChecked) {
                        paymentMethod = "QRIS"
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val denominations =
            listOf("1.000", "2.000", "5.000", "10.000", "20.000", "50.000", "100.000", "Uang Pas")
        denominationAdapter.setData(denominations)
        binding.formPayment.rvDenomination.apply {
            layoutManager = GridLayoutManager(this@PaymentActivity, 2)
            adapter = denominationAdapter
            addItemDecoration(SpacingItemDecoration(24))
        }
    }

    private fun onDenominationClicked(value: String) {
        if (value == "Uang Pas") {
            paymentAmount = totalPrice
        } else {
            val cleanValue = value.replace("[^\\d]".toRegex(), "").toDoubleOrNull() ?: 0.0
            paymentAmount += cleanValue
        }

        binding.formPayment.etTotalPayment.setText(paymentAmount.toFloat().toCurrencyFormat())
        calculateChange()
    }

    private fun calculateChange() {
        val change = paymentAmount - totalPrice

        if (change < 0) {
            binding.formPayment.tilTotalPayment.error =
                getString(R.string.payment_amount_not_enough)
        } else {
            binding.formPayment.tilTotalPayment.error = null
            binding.tvTotalChange.text = change.toFloat().toCurrencyFormat()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_TOTAL_BILL = "extra_total_bill"
        fun startActivity(context: Context, totalPrice: Double) {
            val intent = Intent(context, PaymentActivity::class.java)
            intent.putExtra(EXTRA_TOTAL_BILL, totalPrice)
            context.startActivity(intent)
        }
    }
}
