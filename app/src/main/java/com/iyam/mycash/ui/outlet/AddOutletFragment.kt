package com.iyam.mycash.ui.outlet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.outlet.OutletRequest
import com.iyam.mycash.databinding.FragmentAddOutletBinding
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddOutletFragment : Fragment() {

    private lateinit var binding: FragmentAddOutletBinding
    private val outletViewModel: OutletViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddOutletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAddOutletResult()
        setOnClickListener()
        setLogoPlaceholder()
        requireActivity().title = getString(R.string.add_outlet)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setOnClickListener() {
        binding.formLayout.btn.setOnClickListener {
            doAddOutlet()
        }
    }

    private fun doAddOutlet() {
        if (isFormValid()) {
            val outletName = binding.formLayout.etOutletName.text.toString().trim()
            val outletType = binding.formLayout.etOutletType.text.toString().trim()
            val outletPhone = binding.formLayout.etOutletPhoneNumber.text.toString().trim()
            val outletAddress = binding.formLayout.etOutletAddress.text.toString().trim()
            val outletDistrict = binding.formLayout.etOutletDistrict.text.toString().trim()
            val outletCity = binding.formLayout.etOutletCity.text.toString().trim()
            val outletProvince = binding.formLayout.etOutletProvince.text.toString().trim()
            outletViewModel.doAddOutlet(
                OutletRequest(
                    outletAddress,
                    outletCity,
                    outletDistrict,
                    outletName,
                    outletPhone,
                    outletProvince,
                    outletType
                )
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

    private fun observeAddOutletResult() {
        outletViewModel.addOutletResult.observe(viewLifecycleOwner) { result ->
            result.proceedWhen(
                doOnSuccess = {
                    binding.formLayout.etOutletName.text?.clear()
                    binding.formLayout.etOutletType.text?.clear()
                    binding.formLayout.etOutletPhoneNumber.text?.clear()
                    binding.formLayout.etOutletAddress.text?.clear()
                    binding.formLayout.etOutletDistrict.text?.clear()
                    binding.formLayout.etOutletCity.text?.clear()
                    binding.formLayout.etOutletProvince.text?.clear()
                    binding.formLayout.btn.isVisible = true
                    binding.formLayout.loading.isVisible = false
                    Toast.makeText(requireActivity(), "Outlet added successfully", Toast.LENGTH_SHORT).show()
                    navigateToOutletList()
                },
                doOnLoading = {
                    binding.formLayout.btn.isVisible = false
                    binding.formLayout.loading.isVisible = true
                },
                doOnError = {
                    binding.formLayout.btn.isVisible = true
                    binding.formLayout.loading.isVisible = false
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun navigateToOutletList() {
        requireActivity().onBackPressedDispatcher.onBackPressed()
    }

    private fun setLogoPlaceholder() {
        binding.formLayout.ivProfilePhoto.isVisible = false
        binding.formLayout.btnAddLogo.isVisible = false
    }
}
