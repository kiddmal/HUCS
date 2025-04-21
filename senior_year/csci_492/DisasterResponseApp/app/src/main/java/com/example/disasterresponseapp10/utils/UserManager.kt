package com.example.disasterresponseapp10.utils

import android.content.Context
import androidx.navigation.NavController
import com.example.disasterresponseapp10.MainActivity
import com.example.disasterresponseapp10.R
import com.example.disasterresponseapp10.models.CivilianUser
import com.example.disasterresponseapp10.models.CoordinatorUser
import com.example.disasterresponseapp10.models.ResponderStatus
import com.example.disasterresponseapp10.models.ResponseSpecialization
import com.example.disasterresponseapp10.models.ResponseTeamUser
import com.example.disasterresponseapp10.models.SafetyStatus
import com.example.disasterresponseapp10.models.User
import com.example.disasterresponseapp10.models.UserPermissions
import com.example.disasterresponseapp10.models.UserRole
import com.example.disasterresponseapp10.repositories.FirebaseUserRepository
import com.example.disasterresponseapp10.repositories.UserRepository
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference
import java.util.Date

object UserManager {
    private var currentUser: User? = null
    private var isImpersonating: Boolean = false
    private var originalRole: UserRole? = null
    private val firestore = FirebaseFirestore.getInstance()
    private var mainActivityRef: WeakReference<MainActivity>? = null

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val userRepository: UserRepository by lazy { FirebaseUserRepository() }

    // Coroutine scope for async operations
    private val managerScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // StateFlow for observing user state changes
    private val _userStateFlow = MutableStateFlow<UserState>(UserState.NotInitialized)
    val userStateFlow: StateFlow<UserState> = _userStateFlow.asStateFlow()

    private val activity: MainActivity?
        get() = mainActivityRef?.get()

    sealed class UserState {
        object NotInitialized : UserState()
        object Loading : UserState()
        data class Authenticated(val user: User) : UserState()
        object NotAuthenticated : UserState()
        data class Error(val message: String) : UserState()
    }

    fun setActivity(activity: MainActivity) {
        mainActivityRef = WeakReference(activity)
    }

    suspend fun initialize() {
        withContext(Dispatchers.IO) {
            _userStateFlow.value = UserState.Loading
            try {
                // Check Firebase Auth state
                auth.currentUser?.let { firebaseUser ->
                    loadUserData(firebaseUser.uid)
                } ?: run {
                    _userStateFlow.value = UserState.NotAuthenticated
                }
            } catch (e: Exception) {
                _userStateFlow.value = UserState.Error(e.message ?: "Initialization failed")
            }
        }
    }

    private suspend fun loadUserData(uid: String) {
        try {
            val result = userRepository.getUser(uid)
            result.fold(
                onSuccess = { user ->
                    currentUser = user
                    _userStateFlow.value = UserState.Authenticated(user)
                },
                onFailure = { exception ->
                    _userStateFlow.value = UserState.Error("Failed to load user data: ${exception.message}")
                }
            )
        } catch (e: Exception) {
            _userStateFlow.value = UserState.Error("Failed to load user data")
        }
    }

    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            _userStateFlow.value = UserState.Loading
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Authentication failed")

            val result = userRepository.getUser(uid)
            result.fold(
                onSuccess = { user ->
                    currentUser = user
                    _userStateFlow.value = UserState.Authenticated(user)

                    // Update toolbar menu
                    activity?.toolbarManager?.updateMenuVisibility()

                    Result.success(user)
                },
                onFailure = { exception ->
                    _userStateFlow.value = UserState.Error(exception.message ?: "Sign in failed")
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            _userStateFlow.value = UserState.Error(e.message ?: "Sign in failed")
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        try {
            auth.signOut()
            currentUser = null
            resetImpersonation()
            _userStateFlow.value = UserState.NotAuthenticated

            // Update toolbar menu
            activity?.toolbarManager?.updateMenuVisibility()

        } catch (e: Exception) {
            _userStateFlow.value = UserState.Error("Sign out failed")
        }
    }

    // Add these functions to UserManager.kt

    suspend fun registerUser(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        phoneNumber: String? = null,
        additionalData: Map<String, Any> = emptyMap()
    ): Result<User> {
        return try {
            _userStateFlow.value = UserState.Loading

            // 1. Create Firebase Authentication account
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to create authentication account")

            // 2. Create the base User object
            val newUser = User(
                id = uid,
                email = email,
                displayName = displayName,
                phoneNumber = phoneNumber,
                role = role,
                createdAt = Date(),
                lastActiveAt = Date()
            )

            // 3. Create appropriate role-specific document
            val result = when (role) {
                UserRole.CIVILIAN -> createCivilianUser(newUser, additionalData)
                UserRole.RESPONSE_TEAM -> createResponseTeamUser(newUser, additionalData)
                UserRole.COORDINATOR -> createCoordinatorUser(newUser, additionalData)
                UserRole.ADMINISTRATOR -> {
                    // Administrators should normally be created through a separate, secured process
                    // For now, just create the base user
                    userRepository.createUser(newUser)
                }
            }

            result.fold(
                onSuccess = {
                    currentUser = newUser
                    _userStateFlow.value = UserState.Authenticated(newUser)
                    Result.success(newUser)
                },
                onFailure = { exception ->
                    // Clean up: Delete the authentication account if user document creation fails
                    try {
                        auth.currentUser?.delete()?.await()
                    } catch (e: Exception) {
                        // Log failure to clean up auth account
                    }
                    _userStateFlow.value = UserState.Error("Failed to create user: ${exception.message}")
                    Result.failure(exception)
                }
            )
        } catch (e: Exception) {
            _userStateFlow.value = UserState.Error("Registration failed: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun createCivilianUser(user: User, additionalData: Map<String, Any>): Result<User> {
        return try {
            // Create base user first
            val baseUserResult = userRepository.createUser(user)

            baseUserResult.fold(
                onSuccess = {
                    // Create CivilianUser with additional data
                    val homeAddress = additionalData["homeAddress"] as? String
                    val civilianUser = CivilianUser(
                        user = user,
                        activeRequests = emptyList(),
                        requestHistory = emptyList(),
                        savedResources = emptyList(),
                        safetyStatus = SafetyStatus.UNKNOWN
                    )

                    // Store CivilianUser in Firestore
                    withContext(Dispatchers.IO) {
                        firestore.collection("civilian_users")
                            .document(user.id)
                            .set(civilianUser)
                            .await()
                    }

                    Result.success(user)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createResponseTeamUser(user: User, additionalData: Map<String, Any>): Result<User> {
        return try {
            // Create base user first
            val baseUserResult = userRepository.createUser(user)

            baseUserResult.fold(
                onSuccess = {
                    // Extract additional data
                    val yearsOfExperience = (additionalData["yearsOfExperience"] as? String)?.toIntOrNull() ?: 0
                    val specializationStr = additionalData["specialization"] as? String ?: "RESCUE"
                    val specialization = try {
                        ResponseSpecialization.valueOf(specializationStr)
                    } catch (e: Exception) {
                        ResponseSpecialization.RESCUE // Default
                    }

                    // Create ResponseTeamUser
                    val responseTeamUser = ResponseTeamUser(
                        user = user,
                        primarySpecialization = specialization,
                        secondarySpecializations = emptyList(),
                        assignedTeamId = null,
                        activeTaskIds = emptyList(),
                        status = ResponderStatus.OFF_DUTY,
                        yearsOfExperience = yearsOfExperience
                    )

                    // Store ResponseTeamUser in Firestore
                    withContext(Dispatchers.IO) {
                        firestore.collection("response_team_users")
                            .document(user.id)
                            .set(responseTeamUser)
                            .await()
                    }

                    Result.success(user)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createCoordinatorUser(user: User, additionalData: Map<String, Any>): Result<User> {
        return try {
            // Create base user first
            val baseUserResult = userRepository.createUser(user)

            baseUserResult.fold(
                onSuccess = {
                    // Extract additional data
                    val managedRegion = additionalData["managedRegion"] as? String
                    val organizationId = additionalData["organizationId"] as? String

                    // Create CoordinatorUser
                    val coordinatorUser = CoordinatorUser(
                        user = user,
                        managedTeams = emptyList(),
                        managedRegions = listOfNotNull(managedRegion),
                        activeOperations = emptyList()
                    )

                    // Store CoordinatorUser in Firestore
                    withContext(Dispatchers.IO) {
                        val data = mapOf(
                            "user" to user,
                            "managedTeams" to emptyList<String>(),
                            "managedRegions" to listOfNotNull(managedRegion),
                            "activeOperations" to emptyList<String>(),
                            "organizationId" to organizationId
                        )

                        firestore.collection("coordinator_users")
                            .document(user.id)
                            .set(data)
                            .await()
                    }

                    Result.success(user)
                },
                onFailure = { Result.failure(it) }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentRole(): UserRole? = currentUser?.role

    fun isAuthenticated(): Boolean = currentUser != null

    fun isAdministrator(): Boolean = currentUser?.role == UserRole.ADMINISTRATOR

    // Role impersonation functions
    fun switchRole(newRole: UserRole) {
        if (canSwitchToRole(newRole)) {
            if (originalRole == null && isAdministrator()) {
                originalRole = currentUser?.role
                isImpersonating = true
            }
            currentUser = currentUser?.copy(role = newRole)
            updateUserStateFlow()
        }
    }

    private fun updateUserStateFlow() {
        currentUser?.let {
            _userStateFlow.value = UserState.Authenticated(it)
        }
    }

    fun isImpersonating(): Boolean = isImpersonating

    private fun canSwitchToRole(newRole: UserRole): Boolean {
        return isAdministrator() || newRole == originalRole
    }

    fun resetImpersonation() {
        if (isImpersonating && originalRole != null) {
            currentUser = currentUser?.copy(role = originalRole!!)
            originalRole = null
            isImpersonating = false
            updateUserStateFlow()
        }
    }

    // Permissions handling
    fun hasPermission(permission: (UserPermissions) -> Boolean): Boolean {
        return permission(getPermissions())
    }

    fun getPermissions(): UserPermissions {
        return when (currentUser?.role) {
            UserRole.ADMINISTRATOR -> UserPermissions(
                canAccessResponseTeam = true,
                canManageResources = true,
                canCoordinateTeams = true,
                canViewAnalytics = true,
                canAccessUserManagement = true
            )
            UserRole.COORDINATOR -> UserPermissions(
                canAccessResponseTeam = true,
                canManageResources = true,
                canCoordinateTeams = true,
                canViewAnalytics = false
            )
            UserRole.RESPONSE_TEAM -> UserPermissions(
                canAccessResponseTeam = true,
                canManageResources = false,
                canCoordinateTeams = false,
                canViewAnalytics = false
            )
            else -> UserPermissions()
        }
    }

    fun observeCurrentUser() = userRepository.observeUser(currentUser?.id ?: "")
}