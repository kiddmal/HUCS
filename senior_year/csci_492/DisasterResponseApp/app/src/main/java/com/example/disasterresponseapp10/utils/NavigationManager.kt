package com.example.disasterresponseapp10.utils

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.models.UserPermissions
import com.example.disasterresponseapp10.models.UserRole
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object NavigationManager {
    // Single source of truth for top-level destinations
    private val TOP_LEVEL_DESTINATIONS = setOf(
        R.id.loginFragment,
        R.id.civilianDashboardFragment,
        R.id.responseTeamDashboardFragment,
        R.id.coordinatorDashboardFragment,
        R.id.unifiedResourceFragment,
        R.id.unifiedCommunicationFragment,
        R.id.notificationsFragment,
        R.id.routeNavigationFragment,
        R.id.userProfileFragment
    )

    // Role-specific dashboard destinations
    private val ROLE_DASHBOARDS = setOf(
        R.id.civilianDashboardFragment,
        R.id.responseTeamDashboardFragment,
        R.id.coordinatorDashboardFragment
    )

    // Bottom nav destinations
    private val BOTTOM_NAV_DESTINATIONS = setOf(
        R.id.unifiedResourceFragment,
        R.id.unifiedCommunicationFragment,
        R.id.notificationsFragment,
        R.id.userProfileFragment
    )

    private var currentNavController: NavController? = null
    private var currentAppBarConfiguration: AppBarConfiguration? = null

    fun getTopLevelDestinations(): Set<Int> = TOP_LEVEL_DESTINATIONS

    suspend fun initialize() {
        // Reset navigation state
        currentNavController = null
        currentAppBarConfiguration = null
    }

    fun registerNavController(navController: NavController, appBarConfig: AppBarConfiguration) {
        currentNavController = navController
        currentAppBarConfiguration = appBarConfig
    }

    fun setupMainNavigation(activity: AppCompatActivity): Pair<NavController, AppBarConfiguration> {
        val navHostFragment = activity.supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS)
        return Pair(navController, appBarConfiguration)
    }

    fun isTopLevelDestination(destinationId: Int?): Boolean {
        return destinationId in TOP_LEVEL_DESTINATIONS
    }

    fun isDashboardDestination(destinationId: Int?): Boolean {
        return destinationId in ROLE_DASHBOARDS
    }

    fun isBottomNavDestination(destinationId: Int?): Boolean {
        return destinationId in BOTTOM_NAV_DESTINATIONS
    }

    fun navigateToDashboard(navController: NavController) {
        // Add authentication check here
        if (UserManager.isAuthenticated()) {
            val destination = getDashboardDestination()
            navController.navigate(destination)
        } else {
            navController.navigate(R.id.loginFragment)
        }
    }

    fun getDashboardDestination(): Int {
        return when (UserManager.getCurrentRole()) {
            UserRole.CIVILIAN -> R.id.civilianDashboardFragment
            UserRole.RESPONSE_TEAM -> R.id.responseTeamDashboardFragment
            UserRole.COORDINATOR, UserRole.ADMINISTRATOR -> R.id.coordinatorDashboardFragment
            null -> R.id.loginFragment
        }
    }

    fun setupBackPressHandling(
        dispatcher: OnBackPressedDispatcher,
        navController: NavController,
        activity: Context,
        onFinish: () -> Unit
    ) {
        dispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentDestinationId = navController.currentDestination?.id
                when {
                    currentDestinationId == null -> onFinish()
                    isDashboardDestination(currentDestinationId) -> {
                        showExitConfirmationDialog(activity) { onFinish() }
                    }
                    isTopLevelDestination(currentDestinationId) -> {
                        // Navigate to appropriate dashboard instead of exiting
                        navigateToDashboard(navController)
                    }
                    else -> handleBackNavigation(navController)
                }
            }
        })
    }

    fun handleBackNavigation(navController: NavController) {
        if (!navController.navigateUp()) {
            navigateToDashboard(navController)
        }
    }

    private fun showExitConfirmationDialog(context: Context, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.exit_app)
            .setMessage(R.string.exit_confirmation)
            .setPositiveButton(R.string.yes) { _, _ -> onConfirm() }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    // Auth-related navigation
    fun navigateWithAuth(
        navController: NavController,
        destinationId: Int,
        requiredPermission: (UserPermissions) -> Boolean,
        onAuthRequired: () -> Unit
    ) {
        if (UserManager.hasPermission(requiredPermission)) {
            navController.navigate(destinationId)
        } else {
            onAuthRequired()
        }
    }

    fun showAuthenticationRequiredDialog(
        context: Context,
        navController: NavController,
        loginActionId: Int = R.id.action_global_to_login
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.auth_required)
            .setMessage(R.string.auth_required_message)
            .setPositiveButton(R.string.login) { _, _ ->
                navController.navigate(loginActionId)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    // Extension function
    fun NavController.showAuthenticationDialog(
        context: Context,
        loginActionId: Int = R.id.action_global_to_login
    ) {
        showAuthenticationRequiredDialog(context, this, loginActionId)
    }
}