package com.example.disasterresponseapp10.fragments.userManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.databinding.FragmentUserProfileBinding
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.repositories.FirebaseUserRepository
import com.example.disasterresponseapp10.utils.UserManager
import com.example.disasterresponseapp10.viewmodels.UserViewModel
import com.example.disasterresponseapp10.viewmodels.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment() {
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(FirebaseUserRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRoleSpinner()
        setupObservers()
        loadUserData()
        setupButtons()
    }

    private fun setupRoleSpinner() {
        val roles = UserRole.entries.toTypedArray()

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles.map { it.displayName }
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerRole.adapter = adapter
        }

        // Disable role spinner for non-admin users
        binding.spinnerRole.isEnabled = UserManager.isAdministrator()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.collect { state ->
                when (state) {
                    is UserViewModel.UserState.Initial -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = false
                    }
                    is UserViewModel.UserState.Loading -> {
                        binding.progressBar.isVisible = true
                        binding.contentLayout.isVisible = false
                    }
                    is UserViewModel.UserState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = true
                        displayUserData(state.user)
                    }
                    is UserViewModel.UserState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.contentLayout.isVisible = false
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun loadUserData() {
        val currentUserId = UserManager.getCurrentUserId()
        if (currentUserId != null) {
            viewModel.getUser(currentUserId)
        } else {
            showError(getString(R.string.user_not_logged_in))
        }
    }

    private fun displayUserData(user: com.example.disasterresponseapp10.models.User) {
        binding.apply {
            // Set edit text values
            etFullName.setText(user.displayName)
            etEmail.setText(user.email)
            etPhone.setText(user.phoneNumber ?: "")

            // Set role in spinner
            val rolePosition = UserRole.entries.indexOfFirst { it == user.role }
            if (rolePosition != -1) {
                spinnerRole.setSelection(rolePosition)
            }

            // Show or hide emergency contacts section based on role
            emergencyContactsSection.isVisible = user.role == UserRole.CIVILIAN

            // Profile image placeholder - in a real app, you might load from Firebase Storage
            // For now, use initials as placeholder
            val initials = user.displayName.split(" ")
                .mapNotNull { it.firstOrNull()?.toString() }
                .take(2)
                .joinToString("")

            tvInitials.text = initials
            tvInitials.isVisible = true

            // Handle location sharing switch
            switchLocationSharing.isChecked = user.isLocationSharingEnabled

            // Set created date
            tvCreatedDate.text = "User since: ${android.text.format.DateFormat.getDateFormat(requireContext()).format(user.createdAt)}"

            // Show/hide admin info section
            adminInfoSection.isVisible = user.role == UserRole.ADMINISTRATOR

            // Handle role-specific fields
            when (user.role) {
                UserRole.CIVILIAN -> {
                    // Show civilian-specific fields
                    civilianSection.isVisible = true
                    responseTeamSection.isVisible = false
                    coordinatorSection.isVisible = false

                    // Load emergency contacts
                    val contactsText = if (user.emergencyContacts.isEmpty()) {
                        "No emergency contacts added"
                    } else {
                        user.emergencyContacts.joinToString("\n") {
                            "${it.name} (${it.relationship ?: "Contact"}): ${it.phoneNumber}"
                        }
                    }
                    tvEmergencyContacts.text = contactsText
                }
                UserRole.RESPONSE_TEAM -> {
                    // Show response team specific fields
                    civilianSection.isVisible = false
                    responseTeamSection.isVisible = true
                    coordinatorSection.isVisible = false

                    // You would typically load this data from the ResponseTeamUser collection
                    // For now, just show placeholder
                    tvSpecialization.text = "Loading specialization..."
                }
                UserRole.COORDINATOR -> {
                    // Show coordinator specific fields
                    civilianSection.isVisible = false
                    responseTeamSection.isVisible = false
                    coordinatorSection.isVisible = true

                    // You would typically load this data from the CoordinatorUser collection
                    // For now, just show placeholder
                    tvManagedRegions.text = "Loading managed regions..."
                }
                UserRole.ADMINISTRATOR -> {
                    // Show admin specific fields
                    civilianSection.isVisible = false
                    responseTeamSection.isVisible = false
                    coordinatorSection.isVisible = false

                    tvAdminPrivileges.text = "- Manage Users\n- Manage Roles\n- System Configuration\n- Analytics Access"
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnUpdateProfile.setOnClickListener {
            updateUserProfile()
        }

        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        binding.btnEditEmergencyContacts.setOnClickListener {
            // Navigate to emergency contacts fragment
            // findNavController().navigate(R.id.action_userProfile_to_emergencyContacts)
            Toast.makeText(requireContext(), "Emergency contacts editing will be implemented in a future update", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserProfile() {
        val currentUser = (viewModel.userState.value as? UserViewModel.UserState.Success)?.user ?: return

        // Create updated user object
        val updatedUser = currentUser.copy(
            displayName = binding.etFullName.text.toString(),
            phoneNumber = binding.etPhone.text.toString().takeIf { it.isNotBlank() },
            isLocationSharingEnabled = binding.switchLocationSharing.isChecked,
        )

        // Check if admin is changing role
        if (UserManager.isAdministrator()) {
            val selectedRolePosition = binding.spinnerRole.selectedItemPosition
            val selectedRole = UserRole.entries[selectedRolePosition]

            if (selectedRole != currentUser.role) {
                // Show confirmation dialog for role change
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Change User Role")
                    .setMessage("Are you sure you want to change this user's role from ${currentUser.role.displayName} to ${selectedRole.displayName}?")
                    .setPositiveButton("Change Role") { _, _ ->
                        val userWithNewRole = updatedUser.copy(role = selectedRole)
                        viewModel.updateUser(userWithNewRole)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                return
            }
        }

        // Update user in Firebase
        viewModel.updateUser(updatedUser)
    }

    private fun showChangePasswordDialog() {
        // Show change password dialog
        Toast.makeText(requireContext(), "Password change functionality will be implemented in a future update", Toast.LENGTH_SHORT).show()
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).setAction("Retry") {
            loadUserData()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}