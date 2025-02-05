package com.iyam.mycash.ui.outlet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.iyam.mycash.R
import com.iyam.mycash.databinding.FragmentOutletListBinding
import com.iyam.mycash.ui.main.MainActivity
import com.iyam.mycash.ui.main.MainViewModel
import com.iyam.mycash.ui.settings.SettingsViewModel
import com.iyam.mycash.utils.ApiException
import com.iyam.mycash.utils.proceedWhen
import org.koin.androidx.viewmodel.ext.android.viewModel

class OutletListFragment : Fragment() {

    private lateinit var binding: FragmentOutletListBinding
    private val outletViewModel: OutletViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()
    private val mainViewModel: MainViewModel by viewModel()
    private val outletAdapter: OutletAdapter by lazy {
        OutletAdapter { outlet ->
            mainViewModel.setOutlet(outlet)
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOutletListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = getString(R.string.outlet_list)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(false)
        }
        observeAllData()
        setOutletsRv()
        setSearchView()
        setOnClickListener()
        setOnRefreshListener()
    }

    private fun setOnRefreshListener() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.svOutlet.setQuery(null, false)
            binding.svOutlet.clearFocus()
            observeAllData()
        }
    }

    private fun setSearchView() {
        binding.svOutlet.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                outletViewModel.getOutletsByUser(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    outletViewModel.getOutletsByUser(newText)
                }
                return false
            }
        })
    }

    private fun setOnClickListener() {
        binding.btn.setOnClickListener {
            navigateToAddOutlet()
        }
    }

    private fun navigateToAddOutlet() {
        val action = OutletListFragmentDirections.actionOutletListFragmentToAddOutletFragment()
        findNavController().navigate(action)
    }

    private fun setOutletsRv() {
        binding.rvOutlet.apply {
            adapter = outletAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeAllData(name: String? = null) {
        mainViewModel.authLiveData.observe(viewLifecycleOwner) { authData ->
            authData?.id?.let { settingsViewModel.getUserById(it) }
        }
        outletViewModel.getOutletsByUser(name)

        settingsViewModel.getUserResult.observe(viewLifecycleOwner) { user ->
            user.proceedWhen(
                doOnSuccess = {
                    outletViewModel.outlets.observe(viewLifecycleOwner) { outlets ->
                        binding.swipeRefresh.isRefreshing = false
                        outlets.proceedWhen(
                            doOnSuccess = {
                                user.payload.let { user ->
                                    binding.tvUserName.text = user?.name
                                    if (user?.image.isNullOrBlank()) {
                                        binding.ivProfilePhoto.load(R.drawable.img_placeholder_profile)
                                    } else {
                                        binding.ivProfilePhoto.load(user?.image)
                                    }
                                }
                                outlets.payload?.let { outlets ->
                                    outletAdapter.setData(outlets)
                                }
                                binding.mainLayout.isVisible = true
                                binding.stateLayout.root.isVisible = false
                                binding.stateLayout.animLoading.isVisible = false
                                binding.stateLayout.llAnimError.isVisible = false
                                binding.stateLayout.tvError.isVisible = false
                            },
                            doOnError = {
                                val errorMessage =
                                    (it.exception as? ApiException)?.getParsedError()?.message
                                        ?: getString(R.string.an_error_occurred)
                                Handler().postDelayed({
                                    binding.svOutlet.setQuery(null, false)
                                }, 3000)
                                binding.mainLayout.isVisible = true
                                binding.btn.isVisible = true
                                binding.svOutlet.isVisible = false
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
                    val errorMessage = (it.exception as? ApiException)?.getParsedError()?.message
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
}
