package com.iyam.mycash.ui.settings.outlet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.iyam.mycash.R
import com.iyam.mycash.databinding.ActivityOutletDataUpdateBinding
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.ui.main.MainActivity
import com.iyam.mycash.ui.outlet.OutletViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class OutletDataUpdateActivity : AppCompatActivity() {

    private val binding: ActivityOutletDataUpdateBinding by lazy {
        ActivityOutletDataUpdateBinding.inflate(
            layoutInflater,
            window.decorView.findViewById(android.R.id.content),
            false
        )
    }
    private val outletViewModel: OutletViewModel by viewModel()
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.outlet_management)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        setupForm()
        observeUpdateResult()
        setOnClickListener()
    }

    private fun observeUpdateResult() {
        outletViewModel.updateOutletResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.formLayout.loading.isVisible = false
                    binding.formLayout.btn.isVisible = true
                    binding.formLayout.btn.isEnabled = false
                    it.payload?.let {
                        Toast.makeText(this, "Outlet updated successfully", Toast.LENGTH_SHORT)
                            .show()
                    }
                    navigateToMain()
                },
                doOnLoading = {
                    binding.formLayout.loading.isVisible = true
                    binding.formLayout.btn.isVisible = false
                },
                doOnError = {
                    binding.formLayout.loading.isVisible = false
                    binding.formLayout.btn.isVisible = true
                    binding.formLayout.btn.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra(MainActivity.EXTRA_DESTINATION, MainActivity.DEST_SETTINGS)
        startActivity(intent)
        finish()
    }

    private fun setOnClickListener() {
        binding.formLayout.btn.setOnClickListener {
            doOutletUpdate()
        }
        binding.formLayout.btnAddLogo.setOnClickListener {
            imagePicker()
        }
    }

    private fun imagePicker() {
        ImagePicker.with(this)
            .cropSquare()
            .galleryOnly()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            val img = uri?.toFile()
            binding.formLayout.ivProfilePhoto.load(uri)
            getFile = img
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        }
    }

    private fun doOutletUpdate() {
        if (isFormValid()) {
            val outletName = binding.formLayout.etOutletName.text.toString().trim()
            val outletType = binding.formLayout.etOutletType.text.toString().trim()
            val outletPhone = binding.formLayout.etOutletPhoneNumber.text.toString().trim()
            val outletAddress = binding.formLayout.etOutletAddress.text.toString().trim()
            val outletDistrict = binding.formLayout.etOutletDistrict.text.toString().trim()
            val outletCity = binding.formLayout.etOutletCity.text.toString().trim()
            val outletProvince = binding.formLayout.etOutletProvince.text.toString().trim()
            val requestFile = getFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part? =
                requestFile?.let {
                    MultipartBody.Part.createFormData(
                        "image",
                        getFile?.name ?: "",
                        it
                    )
                }

            val id = intent.getParcelableExtra<Outlet>(EXTRA_OUTLET)?.id.orEmpty()
            val name = outletName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val type = outletType.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val phone = outletPhone.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val address = outletAddress.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val district = outletDistrict.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val city = outletCity.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val province = outletProvince.toRequestBody("multipart/form-data".toMediaTypeOrNull())

            outletViewModel.doUpdateOutlet(
                id,
                name,
                type,
                phone,
                address,
                district,
                city,
                province,
                imageMultipart
            )
        }
    }

    private fun isFormValid(): Boolean {
        val outletName = binding.formLayout.etOutletName.text.toString().trim()
        val outletType = binding.formLayout.etOutletType.text.toString().trim()
        val outletPhone = binding.formLayout.etOutletPhoneNumber.text.toString().trim()
        val outletAddress = binding.formLayout.etOutletAddress.text.toString().trim()
        val outletDistrict = binding.formLayout.etOutletDistrict.text.toString().trim()
        val outletCity = binding.formLayout.etOutletCity.text.toString().trim()
        val outletProvince = binding.formLayout.etOutletProvince.text.toString().trim()
        return checkOutletNameValidation(outletName) &&
            checkOutletTypeValidation(outletType) &&
            checkOutletPhoneValidation(outletPhone) &&
            checkOutletAddressValidation(outletAddress) &&
            checkOutletDistrictValidation(outletDistrict) &&
            checkOutletCityValidation(outletCity) &&
            checkOutletProvinceValidation(outletProvince)
    }

    private fun checkOutletProvinceValidation(outletProvince: String): Boolean {
        return if (outletProvince.isEmpty()) {
            binding.formLayout.tilOutletProvince.isErrorEnabled = true
            binding.formLayout.tilOutletProvince.error =
                getString(R.string.text_error_outlet_province_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletProvince.isErrorEnabled = false
            true
        }
    }

    private fun checkOutletCityValidation(outletCity: String): Boolean {
        return if (outletCity.isEmpty()) {
            binding.formLayout.tilOutletCity.isErrorEnabled = true
            binding.formLayout.tilOutletCity.error =
                getString(R.string.text_error_outlet_city_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletCity.isErrorEnabled = false
            true
        }
    }

    private fun checkOutletDistrictValidation(outletDistrict: String): Boolean {
        return if (outletDistrict.isEmpty()) {
            binding.formLayout.tilOutletDistrict.isErrorEnabled = true
            binding.formLayout.tilOutletDistrict.error =
                getString(R.string.text_error_outlet_district_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletDistrict.isErrorEnabled = false
            true
        }
    }

    private fun checkOutletAddressValidation(outletAddress: String): Boolean {
        return if (outletAddress.isEmpty()) {
            binding.formLayout.tilOutletAddress.isErrorEnabled = true
            binding.formLayout.tilOutletAddress.error =
                getString(R.string.text_error_outlet_address_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletAddress.isErrorEnabled = false
            true
        }
    }

    private fun checkOutletPhoneValidation(outletPhone: String): Boolean {
        return if (outletPhone.isEmpty()) {
            binding.formLayout.tilOutletPhoneNumber.isErrorEnabled = true
            binding.formLayout.tilOutletPhoneNumber.error =
                getString(R.string.text_error_outlet_phone_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletPhoneNumber.isErrorEnabled = false
            true
        }
    }

    private fun checkOutletTypeValidation(outletType: String): Boolean {
        return if (outletType.isEmpty()) {
            binding.formLayout.tilOutletType.isErrorEnabled = true
            binding.formLayout.tilOutletType.error =
                getString(R.string.text_error_outlet_type_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletType.isErrorEnabled = false
            true
        }
    }

    private fun checkOutletNameValidation(outletName: String): Boolean {
        return if (outletName.isEmpty()) {
            binding.formLayout.tilOutletName.isErrorEnabled = true
            binding.formLayout.tilOutletName.error =
                getString(R.string.text_error_outlet_name_cannot_empty)
            false
        } else {
            binding.formLayout.tilOutletName.isErrorEnabled = false
            true
        }
    }

    private fun setupForm() {
        binding.formLayout.ivProfilePhoto.isVisible = true
        binding.formLayout.btnAddLogo.isVisible = true

        val outlet = intent.getParcelableExtra<Outlet>(EXTRA_OUTLET)
        outlet?.let {
            binding.formLayout.etOutletName.setText(it.name)
            binding.formLayout.etOutletType.setText(it.type)
            binding.formLayout.etOutletPhoneNumber.setText(it.phoneNumber)
            binding.formLayout.etOutletAddress.setText(it.address)
            binding.formLayout.etOutletDistrict.setText(it.district)
            binding.formLayout.etOutletCity.setText(it.city)
            binding.formLayout.etOutletProvince.setText(it.province)
            if (it.image.isNullOrBlank()) {
                binding.formLayout.ivProfilePhoto.load(R.drawable.img_placeholder_profile)
            } else {
                binding.formLayout.ivProfilePhoto.load(it.image)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        private const val EXTRA_OUTLET = "EXTRA_OUTLET"
        fun startActivity(context: Context, outlet: Outlet?) {
            val intent = Intent(context, OutletDataUpdateActivity::class.java)
            intent.putExtra(EXTRA_OUTLET, outlet)
            context.startActivity(intent)
        }
    }
}
