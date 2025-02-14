package com.iyam.mycash.ui.pointofsale.session

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
import coil.load
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.recap.SessionDataRecap
import com.iyam.mycash.data.network.api.model.session.RecapSessionRequest
import com.iyam.mycash.databinding.ActivitySessionDetailBinding
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.PointOfSaleViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.createBitmapFromView
import com.iyam.mycash.utils.createQRCode
import com.iyam.mycash.utils.formatDayOnly
import com.iyam.mycash.utils.formatDayWithHours
import com.iyam.mycash.utils.getTodayDate
import com.iyam.mycash.utils.proceedWhen
import com.iyam.mycash.utils.saveBitmapToStorage
import com.iyam.mycash.utils.toCurrencyFormat
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.ByteArrayOutputStream

class SessionDetailActivity : AppCompatActivity() {

    private val binding: ActivitySessionDetailBinding by lazy {
        ActivitySessionDetailBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val mainViewModel: MainViewModel by viewModel()
    private val posViewModel: PointOfSaleViewModel by viewModel()
    private var session: SessionDataRecap? = null
    private var outlet: Outlet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.session_detail)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        setupContent()
        setOnClickListener()
        observeRecapResult()
        observeUploadReceiptResult()
    }

    private fun observeUploadReceiptResult() {
        posViewModel.uploadSessionImageResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
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

    private fun doShowQRDialog(payload: String?) {
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
        val qrImage = payload?.let { createQRCode(it, 500, 500) }
        ivQR.load(qrImage)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun observeRecapResult() {
        posViewModel.updateSessionResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.recapLoading.isVisible = false
                    binding.recapBtn.isVisible = true
                    binding.recapBtn.isEnabled = false
                    it.payload?.let {
                        Toast.makeText(this, "Recap session successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    mainViewModel.removeSession()
                    finish()
                },
                doOnLoading = {
                    binding.recapLoading.isVisible = true
                    binding.recapBtn.isVisible = false
                },
                doOnError = {
                    binding.recapLoading.isVisible = false
                    binding.recapBtn.isVisible = true
                    binding.recapBtn.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setOnClickListener() {
        binding.recapBtn.setOnClickListener {
            doRecap()
        }
        binding.printBtn.setOnClickListener {
            doPrint()
        }
    }

    private fun doPrint() {
        val bitmap = createBitmapFromView(binding.mainReceipt)
        val savedFile = saveBitmapToStorage(this, bitmap, session?.sessionId.orEmpty())
        if (savedFile != null) {
            Toast.makeText(this, "File saved successfully", Toast.LENGTH_LONG)
                .show()
        } else {
            Toast.makeText(this, "Failed to save file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun doRecap() {
        val date = getTodayDate()
        posViewModel.doUpdateSession(
            session?.sessionId.orEmpty(),
            RecapSessionRequest(date)
        )
    }

    private fun setupContent() {
        mainViewModel.outletLiveData.observe(this) {
            outlet = it
            session = intent.getParcelableExtra(EXTRA_SESSION)
            session?.let {
                binding.tvOutletName.text = outlet?.name
                binding.tvOutletAddress.text = outlet?.address
                binding.tvOutletCity.text = outlet?.city
                binding.tvOutletPhoneNumber.text = outlet?.phoneNumber
                binding.tvDate.text = formatDayOnly(it.date.orEmpty())
                binding.tvShift.text =
                    if (it.shift == "MORNING") getString(R.string.morning) else getString(R.string.evening)
                binding.tvUser.text = it.user?.name
                binding.tvStartingCash.text = it.startingCash?.toFloat()?.toCurrencyFormat()
                binding.tvRevenue.text = it.totalRevenue?.toFloat()?.toCurrencyFormat()
                binding.tvTotalTransaction.text =
                    getString(R.string.transaction_count, it.totalTransactions)
                binding.tvSuccessfulTransaction.text =
                    getString(R.string.transaction_count, it.successfulTransactions)
                binding.tvVoidedTransaction.text =
                    getString(R.string.transaction_count, it.voidedTransactions)
                binding.tvCash.text = it.paymentSummary?.cash?.toFloat()?.toCurrencyFormat()
                binding.tvQris.text = it.paymentSummary?.qris?.toFloat()?.toCurrencyFormat()
                if (it.checkOutTime.isNullOrBlank()) {
                    binding.tvRecapBy.isVisible = false
                    binding.recapBtn.isVisible = true
                } else {
                    binding.tvRecapBy.text =
                        getString(R.string.recap_by, formatDayWithHours(it.checkOutTime))
                    binding.recapBtn.isVisible = false
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.share_session_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_session) doShareReceipt()
        return super.onOptionsItemSelected(item)
    }

    private fun doShareReceipt() {
        val bitmap = createBitmapFromView(binding.mainReceipt)
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val timestamp = System.currentTimeMillis()
        val fileName = "${session?.sessionId.orEmpty()}_$timestamp.jpg"
        val requestFile = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())

        val imageMultipart = MultipartBody.Part.createFormData(
            "image",
            fileName,
            requestFile
        )
        posViewModel.uploadSessionImage(session?.sessionId.orEmpty(), imageMultipart)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val EXTRA_SESSION = "EXTRA_SESSION"
        fun startActivity(context: Context, session: SessionDataRecap) {
            val intent = Intent(context, SessionDetailActivity::class.java)
            intent.putExtra(EXTRA_SESSION, session)
            context.startActivity(intent)
        }
    }
}
