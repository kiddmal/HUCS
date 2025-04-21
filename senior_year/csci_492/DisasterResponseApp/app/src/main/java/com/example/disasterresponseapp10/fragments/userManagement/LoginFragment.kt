package com.example.disasterresponseapp10.fragments.userManagement

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.FragmentLoginBinding
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.utils.NavigationManager
import com.example.disasterresponseapp10.utils.UserManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeAuthState()
    }

    private fun setupUI() {
        binding.apply {
            btnLogin.setOnClickListener {
                if (validateInput()) {
                    attemptLogin()
                }
            }
            btnRegister.setOnClickListener {
                findNavController().navigate(R.id.action_login_to_signUp)
            }
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true
        binding.apply {
            if (etEmail.text.isNullOrBlank()) {
                etEmail.error = "Email is required"
                isValid = false
            }
            if (etPassword.text.isNullOrBlank()) {
                etPassword.error = "Password is required"
                isValid = false
            }
        }
        return isValid
    }

    private fun attemptLogin() {
        binding.progressBar.isVisible = true
        binding.btnLogin.isEnabled = false

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val result = UserManager.signIn(
                    binding.etEmail.text.toString().trim(),
                    binding.etPassword.text.toString()
                )

                result.fold(
                    onSuccess = { user ->
                        // Ensure the role is correctly set in the User object
                        Log.d("LoginDebug", "User role: ${user.role}")
                        val destination = when (user.role) {
                            UserRole.CIVILIAN -> R.id.action_login_to_civilianDashboard
                            UserRole.RESPONSE_TEAM -> R.id.action_login_to_responseTeamDashboard
                            UserRole.COORDINATOR, UserRole.ADMINISTRATOR -> R.id.action_login_to_coordinatorDashboard
                        }
                        Log.d("LoginDebug", "Navigating to destination: $destination")
                        findNavController().navigate(destination)
                    },
                    onFailure = { exception ->
                        showError("Login failed: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                showError("Error signing in: ${e.message}")
            } finally {
                binding.progressBar.isVisible = false
                binding.btnLogin.isEnabled = true
            }
        }
    }
    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            UserManager.userStateFlow.collect { state ->
                when (state) {
                    is UserManager.UserState.Authenticated -> {
                        // Updated to handle administrator role explicitly
                        val destination = when (state.user.role) {
                            UserRole.CIVILIAN -> R.id.action_login_to_civilianDashboard
                            UserRole.RESPONSE_TEAM -> R.id.action_login_to_responseTeamDashboard
                            UserRole.COORDINATOR -> R.id.action_login_to_coordinatorDashboard
                            UserRole.ADMINISTRATOR -> R.id.action_login_to_coordinatorDashboard
                        }
                        findNavController().navigate(destination)
                    }
                    is UserManager.UserState.Error -> {
                        showError(state.message)
                    }
                    else -> {
                        // Handle other states if needed
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}