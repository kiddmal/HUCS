package com.example.disasterresponseapp10.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.disasterresponseapp10.models.User
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.repositories.UserRepository
import com.example.disasterresponseapp10.utils.UserManager

class UserViewModel(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _userState = MutableStateFlow<UserState>(UserState.Initial)
    val userState: StateFlow<UserState> = _userState

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    sealed class UserState {
        object Initial : UserState()
        object Loading : UserState()
        data class Success(val user: User) : UserState()
        data class Error(val message: String) : UserState()
    }

    fun createUser(user: User) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val result = userRepository.createUser(user)
                result.fold(
                    onSuccess = {
                        _userState.value = UserState.Success(it)
                        loadUsers()
                    },
                    onFailure = {
                        _userState.value = UserState.Error(it.message ?: "Failed to create user")
                    }
                )
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun getUser(id: String) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val result = userRepository.getUser(id)
                result.fold(
                    onSuccess = { _userState.value = UserState.Success(it) },
                    onFailure = { _userState.value = UserState.Error(it.message ?: "Failed to get user") }
                )
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val result = userRepository.updateUser(user)
                result.fold(
                    onSuccess = {
                        _userState.value = UserState.Success(it)
                        loadUsers()
                    },
                    onFailure = { _userState.value = UserState.Error(it.message ?: "Failed to update user") }
                )
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val result = userRepository.deleteUser(id)
                result.fold(
                    onSuccess = {
                        _userState.value = UserState.Initial
                        loadUsers()
                    },
                    onFailure = { _userState.value = UserState.Error(it.message ?: "Failed to delete user") }
                )
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadUsers(role: UserRole? = null) {
        viewModelScope.launch {
            try {
                val result = if (role != null) {
                    userRepository.getUsersByRole(role)
                } else {
                    // Load all users or handle differently based on current user's role
                    userRepository.getUsersByRole(UserRole.CIVILIAN)
                }

                result.fold(
                    onSuccess = { _users.value = it },
                    onFailure = { _userState.value = UserState.Error(it.message ?: "Failed to load users") }
                )
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun observeUser(id: String) {
        viewModelScope.launch {
            userRepository.observeUser(id).collect { user ->
                user?.let {
                    _userState.value = UserState.Success(it)
                }
            }
        }
    }
    // Add these functions to UserViewModel.kt

    fun signUp(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        phoneNumber: String? = null,
        additionalData: Map<String, Any> = emptyMap()
    ) {
        viewModelScope.launch {
            _userState.value = UserState.Loading
            try {
                val result = UserManager.registerUser(
                    email = email,
                    password = password,
                    displayName = displayName,
                    role = role,
                    phoneNumber = phoneNumber,
                    additionalData = additionalData
                )

                result.fold(
                    onSuccess = {
                        _userState.value = UserState.Success(it)
                    },
                    onFailure = {
                        _userState.value = UserState.Error(it.message ?: "Registration failed")
                    }
                )
            } catch (e: Exception) {
                _userState.value = UserState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun validateRegistrationInput(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        additionalData: Map<String, Any>
    ): ValidationResult {
        val errors = mutableListOf<String>()

        if (email.isBlank()) {
            errors.add("Email is required")
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errors.add("Invalid email format")
        }

        if (password.isBlank()) {
            errors.add("Password is required")
        } else if (password.length < 6) {
            errors.add("Password must be at least 6 characters")
        }

        if (displayName.isBlank()) {
            errors.add("Display name is required")
        }

        // Role-specific validation
        when (role) {
            UserRole.RESPONSE_TEAM -> {
                val yearsExp = additionalData["yearsOfExperience"] as? String
                if (yearsExp.isNullOrBlank()) {
                    errors.add("Years of experience is required for Response Team members")
                }
            }
            UserRole.COORDINATOR -> {
                val managedRegion = additionalData["managedRegion"] as? String
                if (managedRegion.isNullOrBlank()) {
                    errors.add("Managed region is required for Coordinators")
                }
            }
            else -> {} // No specific validation for CIVILIAN or ADMINISTRATOR
        }

        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }

    sealed class ValidationResult {
        object Success : ValidationResult()
        data class Error(val errors: List<String>) : ValidationResult()
    }
}