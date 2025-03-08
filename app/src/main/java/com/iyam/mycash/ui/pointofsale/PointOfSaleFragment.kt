package com.iyam.mycash.ui.pointofsale

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.iyam.mycash.R
import com.iyam.mycash.databinding.FragmentPointOfSaleBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.pointofsale.session.AddSessionActivity
import com.iyam.mycash.ui.pointofsale.session.SessionActivity
import com.iyam.mycash.ui.pointofsale.transaction.productlist.TransactionProductListActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PointOfSaleFragment : Fragment() {

    private lateinit var binding: FragmentPointOfSaleBinding
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPointOfSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.point_of_sale)
        setPointOfSaleItem()
        setOnClickListener()
        observeSession()
    }

    private fun observeSession() {
        mainViewModel.sessionLiveData.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.transactionManagement.btn.setOnClickListener {
                    navigateToTransaction()
                }
                binding.sessionManagement.btn.setOnClickListener {
                    showSessionDialog()
                }
            } else {
                binding.transactionManagement.btn.setOnClickListener {
                    showNoSessionDialog()
                }
                binding.sessionManagement.btn.setOnClickListener {
                    navigateToAddSession()
                }
            }
        }
    }

    private fun showNoSessionDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.cv_background)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tv_desc)
        val btnNegative = dialogView.findViewById<TextView>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<TextView>(R.id.btn_positive)

        tvTitle.text = getString(R.string.active_session_not_found)
        tvDesc.text = getString(R.string.add_new_session_dialog_desc)
        btnNegative.text = getString(R.string.cancel)
        btnPositive.text = getString(R.string.add_new_session)

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        btnPositive.setOnClickListener {
            navigateToAddSession()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun showSessionDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.layout_dialog, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        dialog.window?.setBackgroundDrawableResource(R.drawable.cv_background)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_title)
        val tvDesc = dialogView.findViewById<TextView>(R.id.tv_desc)
        val btnNegative = dialogView.findViewById<TextView>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<TextView>(R.id.btn_positive)

        tvTitle.text = getString(R.string.active_session_found)
        tvDesc.text = getString(R.string.add_active_session_dialog_desc)
        btnNegative.text = getString(R.string.cancel)
        btnPositive.text = getString(R.string.add_new_transaction)

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        btnPositive.setOnClickListener {
            navigateToTransaction()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun navigateToTransaction() {
        val intent = Intent(requireContext(), TransactionProductListActivity::class.java)
        requireActivity().startActivity(intent)
    }

    private fun setOnClickListener() {
        binding.sessionManagement.root.setOnClickListener {
            navigateToSession()
        }
        binding.transactionManagement.root.setOnClickListener {
            navigateToTransaction()
        }
    }

    private fun navigateToAddSession() {
        val intent = Intent(requireContext(), AddSessionActivity::class.java)
        requireActivity().startActivity(intent)
    }

    private fun navigateToSession() {
        val intent = Intent(requireContext(), SessionActivity::class.java)
        requireActivity().startActivity(intent)
    }

    private fun setPointOfSaleItem() {
        binding.sessionManagement.tvManageTitle.text = getString(R.string.session)
        binding.sessionManagement.btn.text = getString(R.string.add_new_session)

        binding.transactionManagement.tvManageTitle.text = getString(R.string.cashier)
        binding.transactionManagement.btn.text = getString(R.string.add_new_transaction)
    }
}
