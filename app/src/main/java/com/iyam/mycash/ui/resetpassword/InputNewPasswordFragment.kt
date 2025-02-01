package com.iyam.mycash.ui.resetpassword

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputLayout
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.resetpassword.ResetPasswordRequest
import com.iyam.mycash.databinding.FragmentInputNewPasswordBinding
import com.iyam.mycash.ui.signin.SignInActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class InputNewPasswordFragment : Fragment() {

    private lateinit var binding: FragmentInputNewPasswordBinding
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModel()
    private val args: InputNewPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputNewPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForm()
        observeResetPasswordResult()
        observeGenerateOtpResult()
        setOnClickListener()
    }

    private fun observeGenerateOtpResult() {
        resetPasswordViewModel.generateOtpResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.loading.isVisible = false
                    binding.tvResendOtp.isVisible = true
                    binding.btn.isVisible = true
                    Toast.makeText(
                        requireContext(),
                        it.payload.orEmpty(),
                        Toast.LENGTH_SHORT
                    ).show()
                },
                doOnLoading = {
                    binding.loading.isVisible = true
                    binding.btn.isVisible = false
                    binding.tvResendOtp.isVisible = false
                },
                doOnError = {
                    binding.loading.isVisible = false
                    binding.tvResendOtp.isVisible = true
                    binding.tvResendOtp.isEnabled = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun observeResetPasswordResult() {
        resetPasswordViewModel.resetPasswordResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    binding.btn.isEnabled = false
                    Toast.makeText(
                        requireContext(),
                        it.payload.orEmpty(),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToSignIn()
                },
                doOnLoading = {
                    binding.loading.isVisible = true
                    binding.btn.isVisible = false
                    binding.tvResendOtp.isVisible = false
                },
                doOnError = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    binding.btn.isEnabled = true
                    binding.tvResendOtp.isVisible = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToSignIn() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun setOnClickListener() {
        binding.btn.setOnClickListener {
            doResetPassword()
        }
        binding.tvResendOtp.setOnClickListener {
            doGenerateOtp()
        }
    }

    private fun doGenerateOtp() {
        val email = binding.formLayout.etEmail.text.toString().trim()
        resetPasswordViewModel.doGenerateOtp(GenerateOtpRequest(email))
    }

    private fun doResetPassword() {
        if (isFormValid()) {
            val email = binding.formLayout.etEmail.text.toString().trim()
            val password = binding.formLayout.etPassword.text.toString().trim()
            val otp = binding.formLayout.etOtp.text.toString().trim()
            resetPasswordViewModel.doResetPassword(ResetPasswordRequest(email, password, otp))
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.formLayout.etEmail.text.toString().trim()
        val password = binding.formLayout.etPassword.text.toString().trim()
        val confirmPassword = binding.formLayout.etConfirmPassword.text.toString().trim()
        val otp = binding.formLayout.etOtp.text.toString().trim()

        return checkEmailValidation(email) &&
            checkPasswordValidation(password, binding.formLayout.tilPassword) &&
            checkPasswordValidation(confirmPassword, binding.formLayout.tilConfirmPassword) &&
            checkPwdAndConfirmPwd(password, confirmPassword) &&
            checkOtpValidation(otp)
    }

    private fun checkOtpValidation(otp: String): Boolean {
        return if (otp.isEmpty()) {
            binding.formLayout.tilOtp.isErrorEnabled = true
            binding.formLayout.tilOtp.error = getString(R.string.text_error_otp_cannot_empty)
            false
        } else if (otp.length < 6) {
            binding.formLayout.tilOtp.isErrorEnabled = true
            binding.formLayout.tilOtp.error = getString(R.string.text_error_otp_less_than_6_char)
            false
        } else {
            true
        }
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

    private fun setupForm() {
        binding.formLayout.tilEmail.isVisible = true
        binding.formLayout.tilEmail.isEnabled = false
        binding.formLayout.tilPassword.isVisible = true
        binding.formLayout.tilConfirmPassword.isVisible = true
        binding.formLayout.tilOtp.isVisible = true
        binding.formLayout.etEmail.setText(args.email)
    }
}
