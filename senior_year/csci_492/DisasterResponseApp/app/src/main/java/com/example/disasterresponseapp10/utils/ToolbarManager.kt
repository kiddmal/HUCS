package com.example.disasterresponseapp10.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.models.UserRole
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class KToolbarManager(
    private val activity: AppCompatActivity,
    private val mainToolbar: MaterialToolbar
) {
    companion object {
        private var isInitialized = false
        private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        suspend fun initialize() {
            withContext(Dispatchers.Main) {
                isInitialized = true
            }
        }

        fun create(activity: AppCompatActivity, toolbar: MaterialToolbar): ToolbarManager {
            require(isInitialized) { "ToolbarManager must be initialized before creation" }
            return ToolbarManager(activity, toolbar)
        }
    }

    fun setup() {
        setupToolbar()
        updateMenuVisibility()
        mainToolbar.invalidateMenu()
    }

    private fun setupToolbar() {
        mainToolbar.apply {
            activity.setSupportActionBar(this)

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_switch_role -> {
                        showRoleSwitcherDialog()
                        true
                    }
                    R.id.action_profile -> {
                        navigateToProfile()
                        true
                    }
                    R.id.notificationsFragment -> {
                        navigateToNotifications()
                        true
                    }
                    R.id.action_logout -> {
                        handleLogout()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun handleLogout() {
        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.logout)
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton(R.string.yes) { _, _ ->
                mainScope.launch {
                    UserManager.signOut()
                    navigateToLogin()
                }
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showRoleSwitcherDialog() {
        val roles = UserRole.entries.toTypedArray()
        val roleNames = roles.map { it.name.replace("_", " ").capitalize() }.toTypedArray()

        MaterialAlertDialogBuilder(activity)
            .setTitle(R.string.switch_role)
            .setItems(roleNames) { _, which ->
                val newRole = roles[which]
                UserManager.switchRole(newRole)
                refreshMenu()
                NavigationManager.navigateToDashboard(activity.findNavController(R.id.nav_host_fragment))
            }
            .show()
    }

    fun updateTitle(title: String) {
        mainToolbar.title = title
    }

    fun handleDestinationChanged(destination: NavDestination) {
        val isTopLevel = NavigationManager.isTopLevelDestination(destination.id)
        mainToolbar.apply {
            if (isTopLevel) {
                navigationIcon = null
            } else {
                setNavigationIcon(R.drawable.ic_back)
                setNavigationOnClickListener {
                    activity.onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        refreshMenu()
    }

    private fun setupRoleSpecificMenu() {
        setupMenuForRole(UserManager.getCurrentRole())
    }

    private fun setupMenuForRole(role: UserRole?) {
        mainToolbar.menu.clear()
        if (role != null) {
            mainToolbar.inflateMenu(R.menu.user_menu)
            mainToolbar.menu.apply {
                findItem(R.id.action_switch_role)?.isVisible = (role == UserRole.ADMINISTRATOR)
                findItem(R.id.action_profile)?.isVisible = true
                findItem(R.id.notificationsFragment)?.isVisible = true
                findItem(R.id.action_logout)?.isVisible = true
            }

            if (UserManager.isImpersonating()) {
                mainToolbar.subtitle = "Impersonating: ${role.name}"
            } else {
                mainToolbar.subtitle = null
            }
        }
    }

    fun refreshMenu() {
        mainToolbar.menu.clear()
        setupMenuForRole(UserManager.getCurrentRole())
    }

    fun updateMenuVisibility() {
        if (UserManager.isAuthenticated()) {
            setupMenuForRole(UserManager.getCurrentRole())
        } else {
            // Clear the menu completely when not logged in
            mainToolbar.menu.clear()
        }
    }

    private fun navigateToLogin() {
        activity.findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_to_login)
    }

    private fun navigateToNotifications() {
        activity.findNavController(R.id.nav_host_fragment).navigate(R.id.notificationsFragment)
    }

    private fun navigateToProfile() {
        activity.findNavController(R.id.nav_host_fragment).navigate(R.id.userProfileFragment)
    }
}