package com.example.disasterresponseapp10.fragments.userManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.FragmentSignUpBinding
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.repositories.FirebaseUserRepository
import com.example.disasterresponseapp10.viewmodels.UserViewModel
import com.example.disasterresponseapp10.viewmodels.UserViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(FirebaseUserRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
    }

    private fun setupUI() {
        setupRoleSpinner()

        // Setup login button
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signUp_to_login)
        }

        // Setup sign up button
        binding.btnSignUp.setOnClickListener {
            signUp()
        }
    }

    private fun setupRoleSpinner() {
        // We exclude ADMINISTRATOR role from the sign-up options
        val roles = listOf(
            UserRole.CIVILIAN,
            UserRole.RESPONSE_TEAM,
            UserRole.COORDINATOR
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles.map { it.displayName }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRole.adapter = adapter

        binding.spinnerRole.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateRoleSpecificFields(roles[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateRoleSpecificFields(role: UserRole) {
        // Show/hide role-specific form sections based on selected role
        binding.apply {
            // Reset visibility of all role-specific layouts
            civilianLayout.isVisible = false
            responseTeamLayout.isVisible = false
            coordinatorLayout.isVisible = false

            // Show the appropriate layout based on role
            when (role) {
                UserRole.CIVILIAN -> civilianLayout.isVisible = true
                UserRole.RESPONSE_TEAM -> responseTeamLayout.isVisible = true
                UserRole.COORDINATOR -> coordinatorLayout.isVisible = true
                else -> {} // Do nothing for ADMINISTRATOR
            }
        }
    }

    private fun getSelectedRole(): UserRole {
        val roleDisplayName = binding.spinnerRole.selectedItem.toString()
        return UserRole.entries.find { it.displayName == roleDisplayName } ?: UserRole.CIVILIAN
    }

    private fun signUp() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val displayName = binding.etDisplayName.text.toString().trim()
        val phoneNumber = binding.etPhone.text.toString().takeIf { it.isNotBlank() }
        val role = getSelectedRole()

        // Collect role-specific data
        val additionalData = mutableMapOf<String, Any>()
        when (role) {
            UserRole.CIVILIAN -> {
                additionalData["homeAddress"] = binding.etHomeAddress.text.toString()
            }
            UserRole.RESPONSE_TEAM -> {
                additionalData["yearsOfExperience"] = binding.etYearsExperience.text.toString()

                // Get selected specialization
                val specializationId = binding.rgSpecialization.checkedRadioButtonId
                val specialization = when (specializationId) {
                    R.id.rbRescue -> "RESCUE"
                    R.id.rbMedical -> "MEDICAL"
                    R.id.rbTransport -> "TRANSPORT"
                    R.id.rbRepair -> "REPAIR"
                    else -> "RESCUE" // Default
                }
                additionalData["specialization"] = specialization
            }
            UserRole.COORDINATOR -> {
                additionalData["managedRegion"] = binding.etManagedRegion.text.toString()
                additionalData["organizationId"] = binding.etOrganizationId.text.toString()
            }
            else -> {}
        }

        // Validate input
        val validationResult = viewModel.validateRegistrationInput(
            email = email,
            password = password,
            displayName = displayName,
            role = role,
            additionalData = additionalData
        )

        when (validationResult) {
            is UserViewModel.ValidationResult.Success -> {
                binding.progressBar.isVisible = true
                binding.btnSignUp.isEnabled = false

                // Proceed with sign up
                viewModel.signUp(
                    email = email,
                    password = password,
                    displayName = displayName,
                    role = role,
                    phoneNumber = phoneNumber,
                    additionalData = additionalData
                )
            }
            is UserViewModel.ValidationResult.Error -> {
                // Show validation errors
                val errorMessage = validationResult.errors.joinToString("\n")
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.collect { state ->
                when (state) {
                    is UserViewModel.UserState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.btnSignUp.isEnabled = false
                    }
                    is UserViewModel.UserState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.btnSignUp.isEnabled = true

                        // Registration successful - show success message and navigate to login
                        Snackbar.make(
                            binding.root,
                            "Account created successfully! Please sign in.",
                            Snackbar.LENGTH_SHORT
                        ).show()

                        // Navigate to login after a short delay
                        binding.root.postDelayed({
                            findNavController().navigate(R.id.action_signUp_to_login)
                        }, 1500)
                    }
                    is UserViewModel.UserState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.btnSignUp.isEnabled = true

                        // Show error message
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        binding.progressBar.isVisible = false
                        binding.btnSignUp.isEnabled = true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}