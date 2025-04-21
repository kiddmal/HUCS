package com.example.disasterresponseapp10.fragments.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.disasterresponseapp10.BaseDashboardFragment
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.adapters.DashboardAdapter
import com.example.disasterresponseapp10.databinding.FragmentDashboardCivilianBinding
import com.example.disasterresponseapp10.models.DashboardCategory
import com.example.disasterresponseapp10.models.DashboardItem
import com.example.disasterresponseapp10.utils.GridSpacingItemDecoration
import com.example.disasterresponseapp10.utils.viewBinding

class CivilianDashboardFragment : BaseDashboardFragment<FragmentDashboardCivilianBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDashboardCivilianBinding {
        return FragmentDashboardCivilianBinding.inflate(inflater, container, false)
    }

    override fun getTitle(): String = getString(R.string.app_name)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEmergencyActions()
    }

    override fun setupDashboard() {
        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = DashboardAdapter(getDashboardItems()) { category ->
                handleCategorySelection(category)
            }
            addItemDecoration(
                GridSpacingItemDecoration(
                    spanCount = 2,
                    spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
                )
            )
        }
    }

    private fun setupEmergencyActions() {
        binding.apply {
            btnSOS.setOnClickListener {
                findNavController().navigate(R.id.action_global_to_sos)
            }

            btnFindShelter.setOnClickListener {
                findNavController().navigate(R.id.action_dashboard_to_resources)
            }
        }
    }

    override fun getDashboardItems(): List<DashboardItem> = listOf(
        DashboardItem(
            category = DashboardCategory.EMERGENCY_SERVICES,
            isEnabled = true
        ),
        DashboardItem(
            category = DashboardCategory.RESOURCES_AID,
            isEnabled = true
        ),
        DashboardItem(
            category = DashboardCategory.COMMUNICATION,
            isEnabled = true
        )
    )

    override fun handleCategorySelection(category: DashboardCategory) {
        when (category) {
            DashboardCategory.RESOURCES_AID ->
                findNavController().navigate(R.id.action_dashboard_to_resources)
            DashboardCategory.COMMUNICATION ->
                findNavController().navigate(R.id.action_dashboard_to_communication)
            else -> { /* Handle other categories */ }
        }
    }
}