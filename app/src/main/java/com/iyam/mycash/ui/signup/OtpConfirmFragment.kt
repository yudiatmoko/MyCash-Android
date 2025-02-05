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
import androidx.navigation.fragment.navArgs
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.user.otp.GenerateOtpRequest
import com.iyam.mycash.data.network.api.model.user.otp.VerifyOtpRequest
import com.iyam.mycash.databinding.FragmentOtpConfirmBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.outlet.OutletActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class OtpConfirmFragment : Fragment() {

    private lateinit var binding: FragmentOtpConfirmBinding
    private val signUpViewModel: SignUpViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val args: OtpConfirmFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForm()
        observeAuthResult()
        observeGenerateOtpResult()
        observeVerifyResult()
        setOnClickListener()
    }

    private fun observeGenerateOtpResult() {
        signUpViewModel.generateOtp.observe(viewLifecycleOwner) {
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

    private fun observeAuthResult() {
        mainViewModel.authLiveData.observe(viewLifecycleOwner) {
            binding.formLayout.etEmail.setText(it?.email.orEmpty())
        }
    }

    private fun setOnClickListener() {
        binding.btn.setOnClickListener {
            doConfirmOtp()
        }
        binding.tvResendOtp.setOnClickListener {
            doGenerateOtp()
        }
    }

    private fun doGenerateOtp() {
        val email = binding.formLayout.etEmail.text.toString().trim()
        signUpViewModel.doGenerateOtp(GenerateOtpRequest(email))
    }

    private fun observeVerifyResult() {
        signUpViewModel.verifyOtp.observe(viewLifecycleOwner) {
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
                    args.authToOtp?.token?.let { token -> mainViewModel.setUserToken(token) }
                    navigateToOutlet()
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

    private fun navigateToOutlet() {
        val intent = Intent(requireContext(), OutletActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun doConfirmOtp() {
        if (isFormValid()) {
            val email = binding.formLayout.etEmail.text.toString().trim()
            val otp = binding.formLayout.etOtp.text.toString().trim()
            signUpViewModel.doVerifyOtp(VerifyOtpRequest(email, otp))
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.formLayout.etEmail.text.toString().trim()
        val otp = binding.formLayout.etOtp.text.toString().trim()

        return checkEmailValidation(email) && checkOtpValidation(otp)
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
        binding.formLayout.tilName.isVisible = false
        binding.formLayout.tilEmail.isVisible = true
        binding.formLayout.tilPassword.isVisible = false
        binding.formLayout.tilConfirmPassword.isVisible = false
        binding.formLayout.tilOtp.isVisible = true
        binding.formLayout.tilEmail.isEnabled = false
    }
}
