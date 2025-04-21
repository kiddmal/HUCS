package com.example.disasterresponseapp10

import android.app.Application
import com.example.disasterresponseapp10.utils.BottomNavManager
import com.example.disasterresponseapp10.utils.NavigationManager
import com.example.disasterresponseapp10.utils.SOSManager
import com.example.disasterresponseapp10.utils.ToolbarManager
import com.example.disasterresponseapp10.utils.UserManager
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DisasterResponseApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        initializeAppCheck()
        appScope.launch {
            initializeManagers()
        }

    }


    private fun initializeAppCheck() {
        // Use the Play Integrity provider for production builds
        // Use the Debug provider for debug builds
        val appCheckProviderFactory = if (BuildConfig.DEBUG) {
            // Debug provider - Only use this for development
            // The debug token will be printed to the console when the app starts
            DebugAppCheckProviderFactory.getInstance()
        } else {
            // Production provider (Play Integrity)
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }

        // Initialize App Check with the appropriate provider
        Firebase.appCheck.installAppCheckProviderFactory(appCheckProviderFactory)
    }

    private suspend fun initializeManagers() = coroutineScope {
        // Launch all initializations in parallel but wait for all to complete
        awaitAll(
            async { UserManager.initialize() },
            async { NavigationManager.initialize() },
            async { ToolbarManager.initialize() },
            async { SOSManager.initialize() },
            async { BottomNavManager.initialize() }
        )
    }
}