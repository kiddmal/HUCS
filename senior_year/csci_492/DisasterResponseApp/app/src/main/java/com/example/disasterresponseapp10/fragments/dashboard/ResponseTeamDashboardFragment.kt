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
import com.example.disasterresponseapp10.databinding.FragmentDashboardResponseTeamBinding
import com.example.disasterresponseapp10.models.DashboardCategory
import com.example.disasterresponseapp10.models.DashboardItem
import com.example.disasterresponseapp10.utils.GridSpacingItemDecoration
import com.example.disasterresponseapp10.utils.UserManager
import com.google.android.material.snackbar.Snackbar

class ResponseTeamDashboardFragment : BaseDashboardFragment<FragmentDashboardResponseTeamBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDashboardResponseTeamBinding {
        return FragmentDashboardResponseTeamBinding.inflate(inflater, container, false)
    }

    override fun getTitle(): String = getString(R.string.response_operations)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!UserManager.hasPermission { it.canAccessResponseTeam }) {
            Snackbar.make(
                view,
                R.string.permission_denied_response_team,
                Snackbar.LENGTH_LONG
            ).show()
            findNavController().navigateUp()
            return
        }
        super.onViewCreated(view, savedInstanceState)
        setupEmergencyActions()
    }

    override fun setupDashboard() {
        binding.categoriesRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int =
                        if (position == 0) 2 else 1
                }
            }
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
        }
    }

    override fun getDashboardItems(): List<DashboardItem> = listOf(
        DashboardItem(
            category = DashboardCategory.RESPONSE_OPERATIONS,
            isEnabled = true,
            statusText = getActiveOperationsCount()
        ),
        DashboardItem(
            category = DashboardCategory.RESOURCES_MANAGEMENT,
            isEnabled = true
        ),
        DashboardItem(
            category = DashboardCategory.TEAM_COMMUNICATION,
            isEnabled = true
        )
    )

    override fun handleCategorySelection(category: DashboardCategory) {
        when (category) {
            DashboardCategory.RESPONSE_OPERATIONS ->
                findNavController().navigate(R.id.responseOperationsFragment)

            DashboardCategory.RESOURCES_MANAGEMENT ->
                findNavController().navigate(R.id.unifiedResourceFragment)

            DashboardCategory.TEAM_COMMUNICATION ->
                findNavController().navigate(R.id.teamChatFragment)

            else -> { /* Handle other categories */
            }
        }
    }

    private fun getActiveOperationsCount(): String? {
        // TODO: Implement getting actual active operations count from repository
        return null
    }
}