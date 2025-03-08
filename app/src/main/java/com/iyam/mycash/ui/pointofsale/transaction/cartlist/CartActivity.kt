package com.iyam.mycash.ui.pointofsale.transaction.cartlist

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityCartBinding
import com.iyam.mycash.model.Cart
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.ui.pointofsale.transaction.PaymentActivity
import com.iyam.mycash.utils.proceedWhen
import com.iyam.mycash.utils.toCurrencyFormat
import org.koin.androidx.viewmodel.ext.android.viewModel

class CartActivity : AppCompatActivity() {

    private val binding: ActivityCartBinding by lazy {
        ActivityCartBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private var totalPrice: Double? = null
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private val cartListAdapter: CartListAdapter by lazy {
        CartListAdapter(
            this,
            object : CartListener {

                override fun onPlusTotalItemCartClicked(cart: Cart) {
                    posViewModel.increaseCart(cart)
                }

                override fun onMinusTotalItemCartClicked(cart: Cart) {
                    posViewModel.decreaseCart(cart)
                }

                override fun onRemoveCartClicked(cart: Cart) {
                    posViewModel.deleteCart(cart)
                }

                override fun onUserDoneEditingQuantity(cart: Cart) {
                    posViewModel.updateQuantity(cart)
                }
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
        title = getString(R.string.cart)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        setupCartList()
        observeData()
        setOnClickListener()
        setSwipeRefresh()
    }

    private fun setSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            observeData()
        }
    }

    private fun setOnClickListener() {
        binding.tvDeleteAll.setOnClickListener {
            showDialog()
        }
        binding.btnCheckout.setOnClickListener {
            totalPrice?.let { totalPrice -> PaymentActivity.startActivity(this, totalPrice) }
        }
    }

    private fun showDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.layout_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.cv_background)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tv_desc)
        val btnNegative = dialogView.findViewById<TextView>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<TextView>(R.id.btn_positive)

        tvTitle.text = getString(R.string.delete_all)
        tvDesc.text = getString(R.string.delete_all_dialog_desc)
        btnNegative.text = getString(R.string.cancel)
        btnPositive.text = getString(R.string.yes)

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        btnPositive.setOnClickListener {
            posViewModel.deleteAllCart()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun observeData() {
        posViewModel.cartList.observe(this) {
            binding.swipeRefresh.isRefreshing = false
            it.proceedWhen(
                doOnSuccess = {
                    binding.mainLayout.isVisible = true
                    binding.rvCart.isVisible = true
                    binding.cvCheckout.isVisible = true
                    binding.tvDeleteAll.isVisible = true
                    binding.stateLayout.root.isVisible = false
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                    it.payload?.let {
                        cartListAdapter.setData(it.first)
                        binding.tvTotalPrice.text = it.second.toFloat().toCurrencyFormat()
                        totalPrice = it.second
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.rvCart.isVisible = false
                    binding.cvCheckout.isVisible = false
                    binding.tvDeleteAll.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnEmpty = {
                    binding.mainLayout.isVisible = true
                    binding.rvCart.isVisible = false
                    binding.cvCheckout.isVisible = false
                    binding.tvDeleteAll.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = true
                    binding.stateLayout.animError.isVisible = true
                    binding.stateLayout.tvError.isVisible = true
                    val errorMessage = getString(R.string.unknown)
                    binding.stateLayout.tvError.text = errorMessage
                    setResult(Activity.RESULT_OK)
                    finish()
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvCart.isVisible = false
                    binding.cvCheckout.isVisible = false
                    binding.tvDeleteAll.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = true
                    binding.stateLayout.animError.isVisible = true
                    binding.stateLayout.tvError.isVisible = true
                    val errorMessage = it.exception?.message ?: getString(R.string.unknown)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setupCartList() {
        binding.rvCart.apply {
            adapter = cartListAdapter
            layoutManager = LinearLayoutManager(this@CartActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
