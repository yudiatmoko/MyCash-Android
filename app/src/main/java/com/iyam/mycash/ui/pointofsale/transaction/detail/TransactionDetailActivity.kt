package com.iyam.mycash.ui.pointofsale.transaction.detail

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityTransactionDetailBinding
import com.iyam.mycash.model.Transaction
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.createBitmapFromView
import com.iyam.mycash.utils.createQRCode
import com.iyam.mycash.utils.formatDayWithHours
import com.iyam.mycash.utils.proceedWhen
import com.iyam.mycash.utils.saveBitmapToStorage
import com.iyam.mycash.utils.toCurrencyFormat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

class TransactionDetailActivity : AppCompatActivity() {

    private val binding: ActivityTransactionDetailBinding by lazy {
        ActivityTransactionDetailBinding.inflate(
            layoutInflater,
            window.decorView as ViewGroup,
            false
        )
    }
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private var transaction: Transaction? = null
    private val detailAdapter: DetailAdapter by lazy {
        DetailAdapter(this)
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
        title = getString(R.string.transaction_detail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        setupContent()
        setupRecyclerView()
        setOnClickListener()
        observeUploadReceiptResult()
        observeVoidTransactionResult()
    }

    private fun observeVoidTransactionResult() {
        posViewModel.voidTransactionResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.llBtnVoid.isVisible = false
                    binding.tvTransactionNumber.text =
                        getString(R.string.session_voided_transaction)
                    binding.tvTransactionNumber.setTextColor(getColor(R.color.md_light_error))
                    it.payload?.let {
                        Toast.makeText(this, "Transaction voided successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                doOnLoading = {
                    binding.llBtnVoid.isVisible = true
                    binding.voidLoading.isVisible = true
                    binding.voidBtn.isVisible = false
                },
                doOnError = {
                    binding.llBtnVoid.isVisible = true
                    binding.voidLoading.isVisible = false
                    binding.voidBtn.isVisible = true
                    binding.voidBtn.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun observeUploadReceiptResult() {
        posViewModel.uploadTransactionImageResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.llBtnVoid.isVisible = false
                    doShowQRDialog(it.payload)
                },
                doOnError = {
                    val errorMessage = (it.exception as? ApiException)?.getParsedError()?.message
                        ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun doShowQRDialog(imageUrl: String?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.layout_qr_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.cv_background)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
        val ivQR = dialogView.findViewById<ImageView>(R.id.iv_qr)
        val btnClose = dialogView.findViewById<TextView>(R.id.btn_close)

        tvTitle.text = getString(R.string.share)
        btnClose.text = getString(R.string.close)
        val qrImage = imageUrl?.let { createQRCode(it, 500, 500) }
        ivQR.load(qrImage)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setOnClickListener() {
        binding.voidBtn.setOnClickListener {
            doVoidTransaction()
        }
        binding.printBtn.setOnClickListener {
            doPrint()
        }
    }

    private fun doPrint() {
        val bitmap = createBitmapFromView(binding.mainReceipt)
        val savedFile = saveBitmapToStorage(this, bitmap, transaction?.id.orEmpty())
        if (savedFile != null) {
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doVoidTransaction() {
        posViewModel.voidTransaction(transaction?.id.orEmpty())
    }

    private fun setupRecyclerView() {
        binding.rvDetails.apply {
            adapter = detailAdapter
            layoutManager = LinearLayoutManager(this@TransactionDetailActivity)
        }
    }

    private fun setupContent() {
        transaction = intent.getParcelableExtra(EXTRA_TRANSACTION)
        transaction.let {
            if (transaction?.isVoided == true) {
                binding.llBtnVoid.isVisible = false
                binding.tvTransactionNumber.text = getString(R.string.session_voided_transaction)
                binding.tvTransactionNumber.setTextColor(getColor(R.color.md_light_error))
            } else {
                binding.llBtnVoid.isVisible = true
                binding.tvTransactionNumber.text = it?.number.toString()
            }
            binding.tvOutletName.text = it?.session?.outlet?.name
            binding.tvOutletAddress.text = it?.session?.outlet?.address
            binding.tvOutletCity.text = it?.session?.outlet?.city
            binding.tvOutletPhoneNumber.text = it?.session?.outlet?.phoneNumber
            binding.tvDate.text = formatDayWithHours(it?.date.toString())
            binding.tvShift.text =
                if (it?.session?.shift == "MORNING") {
                    getString(R.string.morning)
                } else {
                    getString(
                        R.string.evening
                    )
                }
            binding.tvCashier.text = it?.session?.user?.name
            binding.tvTotalPrice.text =
                it?.totalPrice?.toFloat()?.toCurrencyFormat()
            binding.tvPaymentMethod.text = if (it?.paymentMethod == "CASH") {
                getString(R.string.cash)
            } else {
                getString(R.string.qris)
            }
            binding.tvTotalPayment.text =
                it?.totalPayment?.toFloat()?.toCurrencyFormat()
            it?.totalPayment?.toFloat()?.toCurrencyFormat()
            binding.tvTotalChange.text =
                it?.totalCharge?.toFloat()?.toCurrencyFormat()
            detailAdapter.setData(it?.details.orEmpty())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_session_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_session) doShareTransaction()
        return super.onOptionsItemSelected(item)
    }

    private fun doShareTransaction() {
        val bitmap = createBitmapFromView(binding.mainReceipt)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val timestamp = System.currentTimeMillis()
        val fileName = "${transaction?.id.orEmpty()}_$timestamp.jpg"
        val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())

        val imageMultipart = MultipartBody.Part.createFormData(
            "image",
            fileName,
            requestFile
        )
        posViewModel.uploadTransactionImage(transaction?.id.orEmpty(), imageMultipart)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val EXTRA_TRANSACTION = "extra_transaction"
        fun startActivity(context: Context, transaction: Transaction) {
            val intent = Intent(context, TransactionDetailActivity::class.java)
            intent.putExtra(EXTRA_TRANSACTION, transaction)
            context.startActivity(intent)
        }
    }
}
