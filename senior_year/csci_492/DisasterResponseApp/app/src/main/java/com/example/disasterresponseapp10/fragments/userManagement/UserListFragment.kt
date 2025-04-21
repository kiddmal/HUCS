package com.example.disasterresponseapp10.fragments.userManagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.adapters.UserListAdapter
import com.example.disasterresponseapp10.databinding.FragmentUserListBinding
import com.example.disasterresponseapp10.models.User
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.repositories.FirebaseUserRepository
import com.example.disasterresponseapp10.utils.FirebaseAdminService
import com.example.disasterresponseapp10.utils.UserManager
import com.example.disasterresponseapp10.viewmodels.UserViewModel
import com.example.disasterresponseapp10.viewmodels.UserViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class UserListFragment : Fragment() {
    private val viewModel: UserViewModel by viewModels {
        UserViewModelFactory(FirebaseUserRepository())
    }
    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!



    private lateinit var userAdapter: UserListAdapter
    private lateinit var firebaseAdminService: FirebaseAdminService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        firebaseAdminService = FirebaseAdminService()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModel()
        loadUsers()
    }

    private fun setupUI() {
        setupRecyclerView()
        setupFab()
        setupSwipeRefresh()
    }

    private fun setupRecyclerView() {
        userAdapter = UserListAdapter(
            onEditClick = { user -> showEditUserDialog(user) },
            onDeleteClick = { user -> showDeleteConfirmation(user) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = userAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupFab() {
        binding.fabAddUser.setOnClickListener {
            showAddUserDialog()
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            loadUsers()
        }
    }

    private fun showAddUserDialog() {
        AddUserDialog().apply {
            onUserAdded = { user, password ->
                createUser(user, password)
            }
        }.show(parentFragmentManager, "AddUserDialog")  // Use parentFragmentManager instead
    }

    private fun createUser(user: User, password: String) {
        if (!UserManager.isAdministrator()) {
            showError(getString(R.string.permission_denied_message))
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.progressBar.isVisible = true

                val result = firebaseAdminService.createUserWithRole(
                    email = user.email,
                    password = password,
                    displayName = user.displayName,
                    role = user.role,
                    phoneNumber = user.phoneNumber
                )

                result.fold(
                    onSuccess = {
                        Snackbar.make(
                            binding.root,
                            "User created successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        loadUsers() // Refresh the list
                    },
                    onFailure = { exception ->
                        showError("Failed to create user: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                showError("Error creating user: ${e.message}")
            } finally {
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun showEditUserDialog(user: User) {
        // TODO: Implement edit user dialog
    }

    private fun showDeleteConfirmation(user: User) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete_user)
            .setMessage(getString(R.string.delete_user_confirmation, user.displayName))
            .setPositiveButton(R.string.delete) { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun deleteUser(user: User) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                binding.progressBar.isVisible = true
                val result = firebaseAdminService.deleteUser(user.id)

                result.fold(
                    onSuccess = {
                        Snackbar.make(
                            binding.root,
                            "User deleted successfully",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        loadUsers() // Refresh the list
                    },
                    onFailure = { exception ->
                        showError("Failed to delete user: ${exception.message}")
                    }
                )
            } catch (e: Exception) {
                showError("Error deleting user: ${e.message}")
            } finally {
                binding.progressBar.isVisible = false
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userState.collect { state ->
                when (state) {
                    is UserViewModel.UserState.Loading -> {
                        binding.progressBar.isVisible = true
                    }
                    is UserViewModel.UserState.Success -> {
                        binding.progressBar.isVisible = false
                        binding.swipeRefresh.isRefreshing = false
                    }
                    is UserViewModel.UserState.Error -> {
                        binding.progressBar.isVisible = false
                        binding.swipeRefresh.isRefreshing = false
                        showError(state.message)
                    }
                    is UserViewModel.UserState.Initial -> {
                        // Initial state, no action needed
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.collect { users ->
                userAdapter.submitList(users)
                binding.emptyView.isVisible = users.isEmpty()
            }
        }
    }

    private fun loadUsers() {
        if (!UserManager.isAdministrator()) {
            showError(getString(R.string.permission_denied_message))
            return
        }
        viewModel.loadUsers()
    }

    private fun showError(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).setAction(R.string.retry) {
            loadUsers()
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}