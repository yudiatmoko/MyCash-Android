package com.iyam.mycash.ui.signin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.user.login.LoginRequest
import com.iyam.mycash.databinding.ActivitySignInBinding
import com.iyam.mycash.ui.main.MainActivity
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.resetpassword.ResetPasswordActivity
import com.iyam.mycash.ui.signup.SignUpActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignInActivity : AppCompatActivity() {

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(
            layoutInflater,
            window.decorView.findViewById(android.R.id.content),
            false
        )
    }
    private val signInViewModel: SignInViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupForm()
        observeLoginResult()
        setOnClickListener()
    }

    private fun observeLoginResult() {
        signInViewModel.loginResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    binding.btn.isEnabled = false
                    it.payload?.let { navigateToMain() }
                    it.payload?.let { data -> mainViewModel.setUserToken(data.token.orEmpty()) }
                    it.payload?.let { data -> mainViewModel.setAuth(data) }
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

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun doLogin() {
        if (isFormValid()) {
            val email = binding.formLayout.etEmail.text.toString().trim()
            val password = binding.formLayout.etPassword.text.toString().trim()
            signInViewModel.doLogin(LoginRequest(email, password))
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.formLayout.etEmail.text.toString().trim()
        val password = binding.formLayout.etPassword.text.toString().trim()

        return checkEmailValidation(email) && checkPasswordValidation(
            password,
            binding.formLayout.tilPassword
        )
    }

    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isEmpty()) {
            binding.formLayout.tilEmail.isErrorEnabled = true
            binding.formLayout.tilEmail.error = getString(R.string.text_error_email_cannot_empty)
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.formLayout.tilEmail.isErrorEnabled = true
            binding.formLayout.tilEmail.error = getString(R.string.text_error_email_invalid)
            false
        } else {
            binding.formLayout.tilEmail.isErrorEnabled = false
            true
        }
    }

    private fun checkPasswordValidation(password: String, tilPassword: TextInputLayout): Boolean {
        return if (password.isEmpty()) {
            tilPassword.isErrorEnabled = true
            tilPassword.error = getString(R.string.text_error_password_cannot_empty)
            false
        } else if (password.length < 8) {
            tilPassword.isErrorEnabled = true
            tilPassword.error = getString(R.string.text_error_password_less_than_8_char)
            false
        } else {
            tilPassword.isErrorEnabled = false
            true
        }
    }

    private fun setOnClickListener() {
        binding.tvSignUp.setOnClickListener {
            navigateToSignUp()
        }
        binding.tvForgotPassword.setOnClickListener {
            navigateToResetPassword()
        }
        binding.btn.setOnClickListener {
            doLogin()
        }
    }

    private fun navigateToResetPassword() {
        val intent = Intent(this, ResetPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun navigateToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun setupForm() {
        binding.formLayout.tilName.isVisible = false
        binding.formLayout.tilEmail.isVisible = true
        binding.formLayout.tilPassword.isVisible = true
        binding.formLayout.tilConfirmPassword.isVisible = false
        binding.formLayout.tilOtp.isVisible = false
    }
}
