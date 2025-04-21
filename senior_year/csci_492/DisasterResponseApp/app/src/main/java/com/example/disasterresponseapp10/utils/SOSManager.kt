package com.example.disasterresponseapp10.utils

import androidx.navigation.NavController
import com.example.disasterresponseapp10.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SOSManager(
    private val navController: NavController,
    private val fabSOS: FloatingActionButton
) {
    fun setup() {
        setupSOSButton()
        setupDestinationListener()
    }

    private fun setupSOSButton() {
        fabSOS.setOnClickListener {
            NavigationManager.navigateWithAuth(
                navController = navController,
                destinationId = R.id.action_global_to_sos,
                requiredPermission = { true },
                onAuthRequired = { /* Should never happen */ }
            )
        }
    }

    private fun setupDestinationListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showSOS = when (destination.id) {
                R.id.loginFragment,
                R.id.sosFragment -> false
                R.id.civilianDashboardFragment,
                R.id.responseTeamDashboardFragment,
                R.id.coordinatorDashboardFragment,
                R.id.unifiedResourceFragment,
                R.id.notificationsFragment,
                R.id.userProfileFragment -> true
                else -> false
            }

            if (showSOS) fabSOS.show() else fabSOS.hide()
        }
    }
    companion object {
        private var isInitialized = false

        suspend fun initialize() {
            withContext(Dispatchers.Main) {
                isInitialized = true
            }
        }
    }
}