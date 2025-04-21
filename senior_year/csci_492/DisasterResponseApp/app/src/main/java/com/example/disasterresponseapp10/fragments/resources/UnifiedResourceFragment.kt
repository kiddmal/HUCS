package com.example.disasterresponseapp10.fragments.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.adapters.ResourceAdapter
import com.example.disasterresponseapp10.databinding.FragmentUnifiedResourceBinding
import com.example.disasterresponseapp10.models.Resource
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.utils.ToolbarManager
import com.example.disasterresponseapp10.utils.UserManager
import com.example.disasterresponseapp10.viewmodels.UnifiedResourceViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class UnifiedResourceFragment : Fragment() {
    private var _binding: FragmentUnifiedResourceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UnifiedResourceViewModel by viewModels()
    private lateinit var resourceAdapter: ResourceAdapter
    private lateinit var toolbarManager: ToolbarManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUnifiedResourceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupObservers()
        viewModel.loadResources()
    }

    private fun setupUI() {
//        toolbarManager.setup()
        setupRecyclerView()
        setupFilterChips()
        setupFabVisibility()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        resourceAdapter = ResourceAdapter { resource ->
            findNavController().navigate(
                UnifiedResourceFragmentDirections.actionResourcesToDetails(resource)
            )
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = resourceAdapter
        }
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { group, _ ->
            val selectedTypes = group.checkedChipIds.mapNotNull { id ->
                when (id) {
                    R.id.chipShelters -> Resource.ResourceType.SHELTER
                    R.id.chipFood -> Resource.ResourceType.FOOD
                    R.id.chipMedical -> Resource.ResourceType.MEDICAL
                    R.id.chipWater -> Resource.ResourceType.WATER
                    else -> null
                }
            }.toSet()
            viewModel.updateFilters(selectedTypes)
        }
    }

    private fun setupFabVisibility() {
        binding.fabAddResource.visibility = when (UserManager.getCurrentRole()) {
            UserRole.ADMINISTRATOR, UserRole.COORDINATOR -> View.VISIBLE
            else -> View.GONE
        }

        binding.fabAddResource.setOnClickListener {
            showAddResourceDialog()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadResources()
        }
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resourceState.collect { state ->
                when (state) {
                    is UnifiedResourceViewModel.ResourceState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.swipeRefresh.isRefreshing = true
                    }
                    is UnifiedResourceViewModel.ResourceState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.swipeRefresh.isRefreshing = false
                        resourceAdapter.submitList(state.resources)
                    }
                    is UnifiedResourceViewModel.ResourceState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.swipeRefresh.isRefreshing = false
                        showError(state.message)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.filteredResources.collect { resources ->
                resourceAdapter.submitList(resources)
            }
        }
    }

    private fun showAddResourceDialog() {
        AddResourceDialog().apply {
            onResourceAdded = { resource ->
                viewModel.addResource(resource)
                Snackbar.make(
                    binding.root,
                    R.string.resource_added_successfully,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }.show(parentFragmentManager, "AddResourceDialog")
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.retry) {
            viewModel.loadResources()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}