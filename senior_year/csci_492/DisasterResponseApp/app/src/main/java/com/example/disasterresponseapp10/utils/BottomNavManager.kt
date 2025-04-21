package com.example.disasterresponseapp10.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import com.example.disasterresponseapp10.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BottomNavManager(
    private val navController: NavController,
    private val bottomNav: BottomNavigationView
) {
    companion object {
        private var isInitialized = false

        suspend fun initialize() {
            withContext(Dispatchers.Main) {
                isInitialized = true
            }
        }
    }

    init {
        require(isInitialized) { "BottomNavManager must be initialized before creation" }
    }
    fun setup() {
        setupBottomNavigation()
        setupDestinationListener()
    }

    private fun setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.civilianDashboardFragment -> {
                    val dashboardId = NavigationManager.getDashboardDestination()
                    navController.navigate(dashboardId)
                    true
                }
                else -> NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }

    private fun setupDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showNav = shouldShowNavigation(destination)
            if (showNav) showBottomNav() else hideBottomNav()
        }
    }

    private fun shouldShowNavigation(destination: NavDestination): Boolean {
        return NavigationManager.isTopLevelDestination(destination.id) &&
                destination.id != R.id.loginFragment
    }

    private fun showBottomNav() {
        bottomNav.post {
            bottomNav.animate()
                .translationY(0f)
                .setDuration(300)
                .start()
        }
    }

    private fun hideBottomNav() {
        bottomNav.post {
            bottomNav.animate()
                .translationY(bottomNav.height.toFloat())
                .setDuration(300)
                .start()
        }
    }
}
