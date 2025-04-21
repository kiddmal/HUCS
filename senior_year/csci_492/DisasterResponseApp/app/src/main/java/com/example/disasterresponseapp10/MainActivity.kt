package com.example.disasterresponseapp10

import android.os.Bundle
import android.view.Menu
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.disasterresponseapp10.databinding.ActivityMainBinding
import com.example.disasterresponseapp10.utils.BottomNavManager
import com.example.disasterresponseapp10.utils.SOSManager
import com.example.disasterresponseapp10.utils.ToolbarManager
import com.example.disasterresponseapp10.utils.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    // Lazy initialization of managers
     val toolbarManager by lazy {
        ToolbarManager.create(this, binding.mainToolbar)
    }
    private val bottomNavManager by lazy {
        BottomNavManager(navController, binding.bottomNavigation)
    }
    private val sosManager by lazy {
        SOSManager(navController, binding.fabSOS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPlayServices()
        setContentView(binding.root)

        // Set activity reference in UserManager
        UserManager.setActivity(this)


        // Set up toolbar properly
        setSupportActionBar(binding.mainToolbar)

        mainScope.launch {
            setupNavigation()
            setupManagers()
            observeAuthState()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Only inflate menu if user is logged in
        if (UserManager.isAuthenticated()) {
            menuInflater.inflate(R.menu.user_menu, menu)
            return true
        }
        return false
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Define top-level destinations
        val topLevelDestinations = setOf(
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

        // Set up AppBarConfiguration with top-level destinations
        appBarConfiguration = AppBarConfiguration(topLevelDestinations)

        // Set up toolbar with Navigation
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        // Setup back press handling
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!navController.navigateUp()) {
                    finish()
                }
            }
        })
    }


    private fun setupManagers() {
        toolbarManager.setup()
        bottomNavManager.setup()
        sosManager.setup()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun observeAuthState() {
        mainScope.launch {
            UserManager.userStateFlow.collect { state ->
                invalidateOptionsMenu() // This will trigger onCreateOptionsMenu again
            }
        }
    }

    private fun checkPlayServices(): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST)?.show()
            }
            return false
        }
        return true
    }
}