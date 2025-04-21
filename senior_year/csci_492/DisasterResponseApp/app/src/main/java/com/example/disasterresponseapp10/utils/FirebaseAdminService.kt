// FirebaseAdminService.kt

package com.example.disasterresponseapp10.utils

import com.example.disasterresponseapp10.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

class FirebaseAdminService(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val usersCollection = firestore.collection("users")
    private val civilianCollection = firestore.collection("civilian_users")
    private val responseTeamCollection = firestore.collection("response_team_users")
    private val coordinatorCollection = firestore.collection("coordinator_users")

    suspend fun createUserWithRole(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        phoneNumber: String? = null
    ): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create authentication user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to create user")

            // Create base user
            val user = User(
                id = uid,
                email = email,
                displayName = displayName,
                phoneNumber = phoneNumber,
                role = role,
                createdAt = Date(),
                lastActiveAt = Date()
            )

            // Create role-specific user document
            when (role) {
                UserRole.CIVILIAN -> createCivilianUser(user)
                UserRole.RESPONSE_TEAM -> createResponseTeamUser(user)
                UserRole.COORDINATOR -> createCoordinatorUser(user)
                UserRole.ADMINISTRATOR -> {} // No additional data needed
            }

            // Store base user data
            usersCollection.document(uid).set(user).await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun createCivilianUser(user: User) = withContext(Dispatchers.IO) {
        val civilianUser = CivilianUser(
            user = user,
            activeRequests = emptyList(),
            requestHistory = emptyList(),
            savedResources = emptyList(),
            safetyStatus = SafetyStatus.UNKNOWN
        )
        civilianCollection.document(user.id).set(civilianUser).await()
    }

    private suspend fun createResponseTeamUser(user: User) = withContext(Dispatchers.IO) {
        val responseTeamUser = ResponseTeamUser(
            user = user,
            primarySpecialization = ResponseSpecialization.RESCUE,
            secondarySpecializations = emptyList(),
            assignedTeamId = null,
            activeTaskIds = emptyList(),
            status = ResponderStatus.OFF_DUTY,
            skills = emptyList(),
            certifications = emptyMap(),
            yearsOfExperience = 0
        )
        responseTeamCollection.document(user.id).set(responseTeamUser).await()
    }

    private suspend fun createCoordinatorUser(user: User) = withContext(Dispatchers.IO) {
        val coordinatorUser = CoordinatorUser(
            user = user,
            managedTeams = emptyList(),
            managedRegions = emptyList(),
            activeOperations = emptyList()
        )
        coordinatorCollection.document(user.id).set(coordinatorUser).await()
    }

    suspend fun updateUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(user.id).set(user).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Delete role-specific data first
            civilianCollection.document(userId).delete().await()
            responseTeamCollection.document(userId).delete().await()
            coordinatorCollection.document(userId).delete().await()

            // Delete base user data
            usersCollection.document(userId).delete().await()

            // Delete authentication user
            auth.currentUser?.delete()?.await()

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUsersByRole(role: UserRole): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersCollection
                .whereEqualTo("role", role)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}