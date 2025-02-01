package com.iyam.mycash.ui.resetpassword

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.databinding.FragmentInputEmailBinding
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class InputEmailFragment : Fragment() {

    private lateinit var binding: FragmentInputEmailBinding
    private val resetPasswordViewModel: ResetPasswordViewModel by viewModel()
    private var emailArgs = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInputEmailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForm()
        observeGenerateOtpResult()
        setOnClickListener()
    }

    private fun observeGenerateOtpResult() {
        resetPasswordViewModel.generateOtpResult.observe(viewLifecycleOwner) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    Toast.makeText(
                        requireContext(),
                        it.payload.orEmpty(),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToInputPassword()
                    binding.formLayout.etEmail.text?.clear()
                },
                doOnLoading = {
                    binding.loading.isVisible = true
                    binding.btn.isVisible = false
                },
                doOnError = {
                    binding.loading.isVisible = false
                    binding.btn.isVisible = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToInputPassword() {
        if (emailArgs.isNotBlank()) {
            val action =
                InputEmailFragmentDirections.actionInputEmailFragmentToInputNewPasswordFragment(
                    emailArgs
                )
            findNavController().navigate(action)
            emailArgs = ""
        }
    }

    private fun setOnClickListener() {
        binding.btn.setOnClickListener {
            doGenerateOtp()
        }
    }

    private fun doGenerateOtp() {
        if (isFormValid()) {
            val email = binding.formLayout.etEmail.text.toString().trim()
            resetPasswordViewModel.doGenerateOtp(GenerateOtpRequest(email))
            emailArgs = email
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.formLayout.etEmail.text.toString().trim()
        return checkEmailValidation(email)
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
        binding.formLayout.tilPassword.isVisible = false
        binding.formLayout.tilConfirmPassword.isVisible = false
        binding.formLayout.tilOtp.isVisible = false
    }
}
