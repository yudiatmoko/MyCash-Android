package com.iyam.mycash.ui.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import coil.load
import com.iyam.mycash.R
import com.iyam.mycash.databinding.FragmentSettingsBinding
import com.iyam.mycash.model.Outlet
import com.iyam.mycash.model.User
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.outlet.OutletActivity
import com.iyam.mycash.ui.outlet.OutletViewModel
import com.iyam.mycash.ui.settings.detail.DetailActivity
import com.iyam.mycash.ui.settings.outlet.OutletDataUpdateActivity
import com.iyam.mycash.ui.settings.user.UserUpdateActivity
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val mainViewModel: MainViewModel by viewModel()
    private val outletViewModel: OutletViewModel by viewModel()
    private val settingViewModel: SettingsViewModel by viewModel()
    private var outletData: Outlet? = null
    private var userData: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.setting)
        setSettingItem()
        observeHeaderData()
        setOnClickListener()
        setOnRefreshListener()
    }

    private fun setOnRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            observeHeaderData()
        }
    }

    private fun navigateToOutletUpdate() {
        OutletDataUpdateActivity.startActivity(requireActivity(), outletData)
    }

    private fun observeHeaderData() {
        mainViewModel.outletLiveData.observe(viewLifecycleOwner) {
            it?.let { outletViewModel.getOutletById(it.id.orEmpty()) }
        }
        mainViewModel.authLiveData.observe(viewLifecycleOwner) {
            it?.let { settingViewModel.getUserById(it.id.orEmpty()) }
        }

        outletViewModel.outlet.observe(viewLifecycleOwner) { outlet ->
            outlet.proceedWhen(
                doOnSuccess = {
                    settingViewModel.getUserResult.observe(viewLifecycleOwner) { user ->
                        binding.swipeRefresh.isRefreshing = false
                        user.proceedWhen(
                            doOnSuccess = {
                                user.payload?.let {
                                    userData = it
                                }
                                outlet.payload?.let {
                                    outletData = it
                                    binding.tvOutletName.text = it.name
                                    binding.tvOutletType.text = it.type
                                    if (it.image.isNullOrBlank()) {
                                        binding.ivOutletLogo.load(R.drawable.img_placeholder_general)
                                    } else {
                                        binding.ivOutletLogo.load(it.image)
                                    }
                                }
                                binding.mainLayout.isVisible = true
                                binding.stateLayout.root.isVisible = false
                                binding.stateLayout.animLoading.isVisible = false
                                binding.stateLayout.llAnimError.isVisible = false
                                binding.stateLayout.tvError.isVisible = false
                            },
                            doOnError = {
                                val errorMessage =
                                    (user.exception as? ApiException)?.getParsedError()?.message
                                        ?: getString(R.string.an_error_occurred)
                                binding.mainLayout.isVisible = false
                                binding.stateLayout.root.isVisible = true
                                binding.stateLayout.animLoading.isVisible = false
                                binding.stateLayout.llAnimError.isVisible = true
                                binding.stateLayout.tvError.isVisible = true
                                binding.stateLayout.tvError.text = errorMessage
                            }
                        )
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    val errorMessage =
                        (outlet.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.an_error_occurred)
                    binding.mainLayout.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = true
                    binding.stateLayout.tvError.isVisible = true
                    binding.stateLayout.tvError.text = errorMessage
                }
            )
        }
    }

    private fun setSettingItem() {
        binding.accountSetting.tvSettingItem.text = getString(R.string.account_management)
        binding.accountSetting.tvSettingDesc.text = getString(R.string.account_management_desc)
        binding.accountSetting.ivSettingIcon.load(R.drawable.round_manage_accounts_24)

        binding.outletSetting.tvSettingItem.text = getString(R.string.outlet_management)
        binding.outletSetting.tvSettingDesc.text = getString(R.string.outlet_management_desc)
        binding.outletSetting.ivSettingIcon.load(R.drawable.round_store_24)

        binding.printerSetting.tvSettingItem.text = getString(R.string.printer_management)
        binding.printerSetting.tvSettingDesc.text =
            getString(R.string.printer_management_desc)
        binding.printerSetting.ivSettingIcon.load(R.drawable.round_print_24)

        binding.billSetting.tvSettingItem.text = getString(R.string.receipt_management)
        binding.billSetting.tvSettingDesc.text = getString(R.string.receipt_management_desc)
        binding.billSetting.ivSettingIcon.load(R.drawable.round_receipt_24)
    }

    private fun setOnClickListener() {
        binding.btnLogout.setOnClickListener {
            showDialog()
        }

        binding.btnOutletList.setOnClickListener {
            mainViewModel.removeOutlet()
            navigateToOutletList()
        }

        binding.profileContent.setOnClickListener {
            navigateToDetail(userData, outletData)
        }

        binding.accountSetting.mainLayout.setOnClickListener {
            navigateToUserUpdate()
        }

        binding.outletSetting.mainLayout.setOnClickListener {
            navigateToOutletUpdate()
        }

        binding.printerSetting.mainLayout.setOnClickListener {
            showSoonDialog()
        }

        binding.billSetting.mainLayout.setOnClickListener {
            showSoonDialog()
        }
    }

    private fun showSoonDialog() {
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_dialog, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tv_desc)
        val btnNegative = dialogView.findViewById<Button>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<Button>(R.id.btn_positive)

        tvTitle.text = getString(R.string.feature_not_available)
        tvDesc.text = getString(R.string.feature_not_available_desc)
        btnNegative.text = getString(R.string.cancel)
        btnPositive.text = getString(R.string.understand)

        btnNegative.isVisible = false
        btnPositive.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showDialog() {
        val dialogView =
            LayoutInflater.from(requireActivity()).inflate(R.layout.layout_dialog, null)
        val dialog = AlertDialog.Builder(requireActivity())
            .setView(dialogView)
            .create()
        dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.cv_background)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tv_desc)
        val btnNegative = dialogView.findViewById<Button>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<Button>(R.id.btn_positive)

        tvTitle.text = getString(R.string.logout)
        tvDesc.text = getString(R.string.logout_desc)
        btnNegative.text = getString(R.string.cancel)
        btnPositive.text = getString(R.string.yes)

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        btnPositive.setOnClickListener {
            doLogout()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun doLogout() {
        mainViewModel.removeUserToken()
        mainViewModel.removeAuth()
        mainViewModel.removeOutlet()
        requireActivity().finish()
    }

    private fun navigateToUserUpdate() {
        UserUpdateActivity.startActivity(requireActivity(), userData)
    }

    private fun navigateToDetail(userData: User?, outletData: Outlet?) {
        DetailActivity.startActivity(requireActivity(), userData, outletData)
    }

    private fun navigateToOutletList() {
        val intent = Intent(requireActivity(), OutletActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
