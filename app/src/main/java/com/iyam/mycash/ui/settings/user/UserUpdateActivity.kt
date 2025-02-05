package com.iyam.mycash.ui.settings.user

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
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
import com.iyam.mycash.databinding.ActivityUserUpdateBinding
import com.iyam.mycash.model.User
import com.iyam.mycash.ui.main.MainActivity
import com.iyam.mycash.ui.settings.SettingsViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class UserUpdateActivity : AppCompatActivity() {

    private val binding: ActivityUserUpdateBinding by lazy {
        ActivityUserUpdateBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private val settingsViewModel: SettingsViewModel by viewModel()
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
        title = getString(R.string.account_management)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        setupForm()
        observeUpdateResult()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.formLayout.btn.setOnClickListener {
            doUserUpdate()
        }
        binding.formLayout.btnAddLogo.setOnClickListener {
            imagePicker()
        }
    }

    private fun doUserUpdate() {
        if (isFormValid()) {
            val userName = binding.formLayout.etName.text.toString().trim()
            val userPhoneNumber = binding.formLayout.etPhoneNumber.text.toString().trim()
            val requestFile = getFile?.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part? =
                requestFile?.let {
                    MultipartBody.Part.createFormData(
                        "image",
                        getFile?.name ?: "",
                        it
                    )
                }
            val id = intent.getParcelableExtra<User>(EXTRA_USER)?.id.orEmpty()
            val name = userName.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val phoneNumber =
                userPhoneNumber.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            settingsViewModel.doUserUpdate(id, name, phoneNumber, imageMultipart)
        }
    }

    private fun isFormValid(): Boolean {
        val name = binding.formLayout.etName.text.toString().trim()
        val phoneNumber = binding.formLayout.etPhoneNumber.text.toString().trim()

        return checkNameValidation(name) &&
            checkPhoneNumberValidation(phoneNumber)
    }

    private fun checkPhoneNumberValidation(phoneNumber: String): Boolean {
        return if (phoneNumber.isEmpty()) {
            binding.formLayout.tilPhoneNumber.isErrorEnabled = true
            binding.formLayout.tilPhoneNumber.error =
                getString(R.string.text_error_phone_cannot_empty)
            false
        } else {
            binding.formLayout.tilPhoneNumber.isErrorEnabled = false
            true
        }
    }

    private fun checkNameValidation(name: String): Boolean {
        return if (name.isEmpty()) {
            binding.formLayout.tilName.isErrorEnabled = true
            binding.formLayout.tilName.error =
                getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.formLayout.tilName.isErrorEnabled = false
            true
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

    private fun observeUpdateResult() {
        settingsViewModel.getUserUpdateResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.formLayout.loading.isVisible = false
                    binding.formLayout.btn.isVisible = true
                    binding.formLayout.btn.isEnabled = false
                    it.payload?.let {
                        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT)
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

    private fun setupForm() {
        binding.formLayout.ivProfilePhoto.isVisible = true
        binding.formLayout.btnAddLogo.isVisible = true

        val user = intent.getParcelableExtra<User>(EXTRA_USER)
        user?.let {
            binding.formLayout.etEmail.isEnabled = false
            binding.formLayout.etName.setText(it.name)
            binding.formLayout.etEmail.setText(it.email)
            if (user.phoneNumber.isNullOrBlank()) {
                binding.formLayout.etPhoneNumber.setText(getString(R.string.unknown))
            } else {
                binding.formLayout.etPhoneNumber.setHint(R.string.outlet_phone_number_example)
            }
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
        private const val EXTRA_USER = "EXTRA_USER"
        fun startActivity(context: Context, user: User?) {
            val intent = Intent(context, UserUpdateActivity::class.java)
            intent.putExtra(EXTRA_USER, user)
            context.startActivity(intent)
        }
    }
}
