package com.iyam.mycash.ui.manage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.iyam.mycash.R
import com.iyam.mycash.databinding.FragmentManageBinding
import com.iyam.mycash.ui.manage.category.CategoryActivity
import com.iyam.mycash.ui.manage.product.AddProductActivity
import com.iyam.mycash.ui.manage.product.ProductListActivity

class ManageFragment : Fragment() {

    private lateinit var binding: FragmentManageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentManageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.manage)
        setManageItem()
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.categoryManagement.root.setOnClickListener {
            val intent = Intent(requireActivity(), CategoryActivity::class.java)
            startActivity(intent)
        }
        binding.categoryManagement.btn.setOnClickListener {
            navigateToAddCategory()
        }

        binding.productManagement.root.setOnClickListener {
            val intent = Intent(requireActivity(), ProductListActivity::class.java)
            startActivity(intent)
        }
        binding.productManagement.btn.setOnClickListener {
            navigateToAddProduct()
        }
    }

    private fun navigateToAddProduct() {
        val intent = Intent(requireActivity(), AddProductActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToAddCategory() {
        CategoryActivity.startActivity(requireActivity(), true)
    }

    private fun setManageItem() {
        binding.productManagement.tvManageTitle.text = getString(R.string.product)
        binding.productManagement.btn.text = getString(R.string.add_new_product)

        binding.categoryManagement.tvManageTitle.text = getString(R.string.category)
        binding.categoryManagement.btn.text = getString(R.string.add_new_category)

        binding.statementManagement.tvManageTitle.text = getString(R.string.statement)
        binding.statementManagement.btn.text = getString(R.string.view_statement)
        val drawable =
            ContextCompat.getDrawable(requireContext(), R.drawable.round_remove_red_eye_24)
        drawable?.setTint(ContextCompat.getColor(requireContext(), R.color.md_dark_primary))
        binding.statementManagement.btn.icon = drawable
    }
}
