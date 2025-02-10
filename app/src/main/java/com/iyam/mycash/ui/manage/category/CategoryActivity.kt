package com.iyam.mycash.ui.manage.category

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputLayout
import com.iyam.mycash.R
import com.iyam.mycash.data.network.api.model.category.CategoryRequest
import com.iyam.mycash.databinding.ActivityCategoryBinding
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.manage.ManageViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategoryActivity : AppCompatActivity() {

    private val binding: ActivityCategoryBinding by lazy {
        ActivityCategoryBinding.inflate(layoutInflater, window.decorView as ViewGroup, false)
    }
    private var outletId: String? = null
    private val manageViewModel: ManageViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter(
            context = this,
            deleteListener = {
                manageViewModel.doDeleteCategory(it.id.orEmpty())
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        title = getString(R.string.category)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.round_arrow_back_ios_24)
        }
        observeData()
        setupRecyclerView()
        observeDeleteCategory()
        observeAddCategory()
        setSearchView()
        setOnRefreshListener()
        addCategoryNavigation()
    }

    private fun setSearchView() {
        binding.svCategory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                manageViewModel.getCategoriesByOutlet(outletId.orEmpty(), query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    manageViewModel.getCategoriesByOutlet(outletId.orEmpty(), newText)
                }
                return false
            }
        })
    }

    private fun observeDeleteCategory() {
        manageViewModel.deleteCategoryResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(this, it.payload, Toast.LENGTH_SHORT).show()
                    observeData()
                },
                doOnError = {
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun setOnRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            observeData()
        }
    }

    private fun observeAddCategory() {
        manageViewModel.addCategoryResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(this, "Add Category Success", Toast.LENGTH_SHORT).show()
                    observeData()
                },
                doOnError = {
                    val errorMessage = (it.exception as? ApiException)?.getParsedError()?.message
                    Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun observeData() {
        mainViewModel.outletLiveData.observe(this) {
            it?.let {
                manageViewModel.getCategoriesByOutlet(it.id.orEmpty())
                outletId = it.id
            }
        }
        manageViewModel.categories.observe(this) {
            binding.swipeRefresh.isRefreshing = false
            it.proceedWhen(
                doOnSuccess = {
                    binding.mainLayout.isVisible = true
                    binding.rvCategory.isVisible = true
                    binding.stateLayout.root.isVisible = false
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                    it.payload?.let {
                        categoryAdapter.setData(it)
                    }
                },
                doOnLoading = {
                    binding.mainLayout.isVisible = false
                    binding.rvCategory.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = true
                    binding.stateLayout.llAnimError.isVisible = false
                    binding.stateLayout.tvError.isVisible = false
                },
                doOnError = {
                    binding.mainLayout.isVisible = true
                    binding.rvCategory.isVisible = false
                    binding.stateLayout.root.isVisible = true
                    binding.stateLayout.animLoading.isVisible = false
                    binding.stateLayout.llAnimError.isVisible = true
                    binding.stateLayout.animError.isVisible = true
                    binding.stateLayout.tvError.isVisible = true
                    val errorMessage =
                        (it.exception as? ApiException)?.getParsedError()?.message
                            ?: getString(R.string.unknown)
                    binding.stateLayout.tvError.text = errorMessage
                }
            )
        }
    }

    private fun setupRecyclerView() {
        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(this@CategoryActivity)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_category_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_category) showAddCategoryDialog()
        return super.onOptionsItemSelected(item)
    }

    private fun showAddCategoryDialog() {
        val dialogView =
            LayoutInflater.from(this).inflate(R.layout.layout_add_category_dialog, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()
        dialog.getWindow()?.setBackgroundDrawableResource(R.drawable.cv_background)
        dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnNegative = dialogView.findViewById<TextView>(R.id.btn_negative)
        val btnPositive = dialogView.findViewById<TextView>(R.id.btn_positive)
        val tilName = dialogView.findViewById<TextInputLayout>(R.id.til_name)
        val etName = dialogView.findViewById<TextView>(R.id.et_name)

        btnNegative.text = getString(R.string.cancel)
        btnPositive.text = getString(R.string.save)

        btnNegative.setOnClickListener {
            dialog.dismiss()
        }
        btnPositive.setOnClickListener {
            mainViewModel.outletLiveData.observe(this) {
                it?.let {
                    if (etName.text.isNotEmpty()) {
                        manageViewModel.doAddCategory(
                            CategoryRequest(
                                name = etName.text.toString(),
                                outletId = it.id.orEmpty()
                            )
                        )
                        dialog.dismiss()
                    } else {
                        tilName.isErrorEnabled = true
                        tilName.error = getString(R.string.category_required)
                    }
                }
            }
        }
        dialog.show()
    }

    private fun addCategoryNavigation() {
        if (intent.getBooleanExtra(ADD_CATEGORY_REQUEST, false)) {
            showAddCategoryDialog()
        }
    }

    companion object {
        private const val ADD_CATEGORY_REQUEST = "ADD_CATEGORY_REQUEST"
        fun startActivity(context: Context, showDialog: Boolean = false) {
            val intent = Intent(context, CategoryActivity::class.java)
            intent.putExtra(ADD_CATEGORY_REQUEST, true)
            context.startActivity(intent)
        }
    }
}
