package com.iyam.mycash.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.user.register.RegisterRequest
import com.iyam.mycash.databinding.FragmentSignUpBinding
import com.iyam.mycash.model.Auth
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.signin.SignInActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private val signUpViewModel: SignUpViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSignUpForm()
        observeRegisterResult()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btn.setOnClickListener {
            doRegister()
        }
        binding.tvSignIn.setOnClickListener {
            navigateToSignIn()
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun observeRegisterResult() {
        signUpViewModel.registerResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    binding.btn.isEnabled = false
                    it.payload?.let { data -> navigateToVerifyOtp(data) }
//                    it.payload?.let { data -> mainViewModel.setUserToken(data.token.orEmpty()) }
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
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToVerifyOtp(auth: Auth) {
        val action = SignUpFragmentDirections.actionSignUpFragmentToOtpConfirmFragment(auth)
        findNavController().navigate(action)
    }

    private fun doRegister() {
        if (isFormValid()) {
            val name = binding.formLayout.etName.text.toString().trim()
            val email = binding.formLayout.etEmail.text.toString().trim()
            val password = binding.formLayout.etPassword.text.toString().trim()
            signUpViewModel.doRegister(RegisterRequest(name, email, password))
        }
    }

    private fun isFormValid(): Boolean {
        val name = binding.formLayout.etName.text.toString().trim()
        val email = binding.formLayout.etEmail.text.toString().trim()
        val password = binding.formLayout.etPassword.text.toString().trim()
        val confirmPassword = binding.formLayout.etConfirmPassword.text.toString().trim()

        return checkNameValidation(name) &&
            checkEmailValidation(email) &&
            checkPasswordValidation(password, binding.formLayout.tilPassword) &&
            checkPasswordValidation(confirmPassword, binding.formLayout.tilConfirmPassword) &&
            checkPwdAndConfirmPwd(password, confirmPassword)
    }

    private fun checkPwdAndConfirmPwd(password: String, confirmPassword: String): Boolean {
        return if (password != confirmPassword) {
            binding.formLayout.tilPassword.isErrorEnabled = true
            binding.formLayout.tilConfirmPassword.isErrorEnabled = true
            binding.formLayout.tilPassword.error = getString(R.string.text_password_does_not_match)
            binding.formLayout.tilConfirmPassword.error =
                getString(R.string.text_password_does_not_match)
            false
        } else {
            binding.formLayout.tilPassword.isErrorEnabled = false
            binding.formLayout.tilConfirmPassword.isErrorEnabled = false
            true
        }
    }

    private fun checkPasswordValidation(
        password: String,
        texInputLayout: TextInputLayout
    ): Boolean {
        return if (password.isEmpty()) {
            texInputLayout.isErrorEnabled = true
            texInputLayout.error = getString(R.string.text_error_password_cannot_empty)
            false
        } else if (password.length < 8) {
            texInputLayout.isErrorEnabled = true
            texInputLayout.error = getString(R.string.text_error_password_less_than_8_char)
            false
        } else {
            texInputLayout.isErrorEnabled = false
            true
        }
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

    private fun setupSignUpForm() {
        binding.formLayout.tilName.isVisible = true
        binding.formLayout.tilEmail.isVisible = true
        binding.formLayout.tilPassword.isVisible = true
        binding.formLayout.tilConfirmPassword.isVisible = true
        binding.formLayout.tilOtp.isVisible = false
    }
}
