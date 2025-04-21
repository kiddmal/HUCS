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
import com.example.disasterresponseapp10.databinding.FragmentDashboardCoordinatorBinding
import com.example.disasterresponseapp10.models.DashboardCategory
import com.example.disasterresponseapp10.models.DashboardItem
import com.example.disasterresponseapp10.utils.GridSpacingItemDecoration
import com.example.disasterresponseapp10.utils.UserManager
import com.example.disasterresponseapp10.utils.viewBinding
import com.google.android.material.snackbar.Snackbar

class CoordinatorDashboardFragment : BaseDashboardFragment<FragmentDashboardCoordinatorBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDashboardCoordinatorBinding {
        return FragmentDashboardCoordinatorBinding.inflate(inflater, container, false)
    }

    override fun getTitle(): String = getString(R.string.coordinator_dashboard)
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

    override fun getDashboardItems(): List<DashboardItem> = listOf(
        DashboardItem(
            category = DashboardCategory.OPERATIONS_OVERVIEW,
            isEnabled = true
        ),
        DashboardItem(
            category = DashboardCategory.TEAM_MANAGEMENT,
            isEnabled = true
        ),
        DashboardItem(
            category = DashboardCategory.RESOURCE_ALLOCATION,
            isEnabled = true
        ),
        DashboardItem(
            category = DashboardCategory.ANALYTICS,
            isEnabled = UserManager.hasPermission { it.canViewAnalytics },
            statusText = if (!UserManager.hasPermission { it.canViewAnalytics }) {
                getString(R.string.requires_admin_permission)
            } else null
        ),
        DashboardItem(
            category = DashboardCategory.USER_MANAGEMENT,
            isEnabled = UserManager.isAdministrator(),
            statusText = if (!UserManager.isAdministrator()) {
                getString(R.string.requires_admin_permission)
            } else null
        )
    )

    override fun handleCategorySelection(category: DashboardCategory) {
        when (category) {
            DashboardCategory.OPERATIONS_OVERVIEW ->
                findNavController().navigate(R.id.responseOperationsFragment)
            DashboardCategory.TEAM_MANAGEMENT ->
                findNavController().navigate(R.id.teamManagementFragment)
            DashboardCategory.RESOURCE_ALLOCATION ->
                findNavController().navigate(R.id.unifiedResourceFragment)
            DashboardCategory.USER_MANAGEMENT -> {
                if (UserManager.hasPermission { it.canAccessUserManagement }) {
                    findNavController().navigate(R.id.userListFragment)
                } else {
                    showPermissionDenied()
                }
            }
            DashboardCategory.ANALYTICS -> {
                if (UserManager.hasPermission { it.canViewAnalytics }) {
                    // Navigate to analytics when implemented
                } else {
                    showPermissionDenied()
                }
            }
            else -> { /* Handle other categories */ }
        }
    }

    private fun showPermissionDenied() {
        Snackbar.make(
            binding.root,
            R.string.permission_denied_coordinator,
            Snackbar.LENGTH_LONG
        ).show()
    }
}