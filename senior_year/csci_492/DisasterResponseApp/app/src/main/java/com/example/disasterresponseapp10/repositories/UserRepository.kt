package com.example.disasterresponseapp10.repositories

import com.example.disasterresponseapp10.models.User
import com.example.disasterresponseapp10.models.CivilianUser
import com.example.disasterresponseapp10.models.ResponseTeamUser
import com.example.disasterresponseapp10.models.CoordinatorUser
import com.example.disasterresponseapp10.models.EmergencyContact
import com.example.disasterresponseapp10.models.ResponseSpecialization
import com.example.disasterresponseapp10.models.UserLocation
import com.example.disasterresponseapp10.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.util.Date

interface UserRepository {
    suspend fun createUser(user: User): Result<User>
    suspend fun getUser(id: String): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun deleteUser(id: String): Result<Boolean>
    suspend fun getUsersByRole(role: UserRole): Result<List<User>>
    fun observeUser(id: String): Flow<User?>
}

class FirebaseUserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : UserRepository {
    private val usersCollection = firestore.collection("users")

    suspend fun createAdminUser(email: String, password: String, displayName: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            // Create authentication user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Failed to create user")

            // Create admin user object
            val adminUser = User(
                id = uid,
                email = email,
                displayName = displayName,
                role = UserRole.ADMINISTRATOR,
                createdAt = Date(),
                lastActiveAt = Date()
            )

            // Store in Firestore with admin specific fields
            val adminData = adminUser.toMap() + mapOf(
                "isAdmin" to true,
                "adminPrivileges" to listOf(
                    "MANAGE_USERS",
                    "MANAGE_ROLES",
                    "VIEW_ANALYTICS",
                    "MANAGE_RESOURCES",
                    "MANAGE_OPERATIONS"
                )
            )

            // Create admin user document
            usersCollection.document(uid).set(adminData).await()

            // Create admin-specific document
            firestore.collection("admin_users")
                .document(uid)
                .set(mapOf(
                    "userId" to uid,
                    "createdAt" to Date(),
                    "lastAccess" to Date()
                )).await()

            Result.success(adminUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyAdminStatus(uid: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userDoc = usersCollection.document(uid).get().await()
            return@withContext userDoc.getBoolean("isAdmin") ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getCurrentUserRole(): UserRole = withContext(Dispatchers.IO) {
        try {
            val user = auth.currentUser ?: throw Exception("No authenticated user")
            val userDoc = usersCollection.document(user.uid).get().await()

            // Check if user is admin first
            val isAdmin = userDoc.getBoolean("isAdmin") ?: false
            if (isAdmin) {
                return@withContext UserRole.ADMINISTRATOR
            }

            // Otherwise get regular role
            val roleString = userDoc.getString("role") ?: "civilian"
            UserRole.fromFirebaseRole(roleString)
        } catch (e: Exception) {
            UserRole.CIVILIAN // Default to civilian on error
        }
    }

    suspend fun hasAdminPrivilege(userId: String, privilege: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val userDoc = usersCollection.document(userId).get().await()
            val privileges = userDoc.get("adminPrivileges") as? List<String> ?: emptyList()
            privileges.contains(privilege)
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val userMap = user.toMap()
            usersCollection.document(user.id).set(userMap).await()

            // Create role-specific document in background
            when (user.role) {
                UserRole.CIVILIAN -> createCivilianUser(user)
                UserRole.RESPONSE_TEAM -> createResponseTeamUser(user)
                UserRole.COORDINATOR -> createCoordinatorUser(user)
                else -> {} // Handle ADMINISTRATOR case if needed
            }

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(id: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val document = usersCollection.document(id).get().await()
            if (document.exists()) {
                val data = document.data
                if (data != null) {
                    val user = User(
                        id = document.id,
                        email = data["email"] as? String ?: "",
                        displayName = data["displayName"] as? String ?: "",
                        phoneNumber = data["phoneNumber"] as? String,
//                        role = UserRole.fromFirebaseRole(data["role"] as? String ?: ""),
                        role = UserRole.fromFirebaseRole(data["role"] as? String ?: ""),
                        lastKnownLocation = convertFirestoreToLocation(data["lastKnownLocation"] as? Map<String, Any>),
                        isLocationSharingEnabled = data["isLocationSharingEnabled"] as? Boolean ?: false,
                        createdAt = (data["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                        lastActiveAt = (data["lastActiveAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                        emergencyContacts = convertFirestoreToEmergencyContacts(data["emergencyContacts"] as? List<Map<String, Any>> ?: emptyList()),
                        deviceToken = data["deviceToken"] as? String
                    )
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data is null"))
                }
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    private fun convertFirestoreToLocation(data: Map<String, Any>?): UserLocation? {
        if (data == null) return null

        return try {
            val coordinates = when (val coords = data["coordinates"]) {
                is List<*> -> coords.filterIsInstance<Number>().map { it.toDouble() }.toDoubleArray()
                else -> doubleArrayOf(0.0, 0.0)
            }

            UserLocation(
                coordinates = coordinates,
                address = data["address"] as? String,
                timestamp = (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                accuracy = (data["accuracy"] as? Number)?.toFloat(),
                isSharing = data["isSharing"] as? Boolean ?: false
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun convertFirestoreToEmergencyContacts(data: List<Map<String, Any>>): List<EmergencyContact> {
        return data.mapNotNull { contactData ->
            try {
                EmergencyContact(
                    id = contactData["id"] as? String ?: return@mapNotNull null,
                    name = contactData["name"] as? String ?: return@mapNotNull null,
                    phoneNumber = contactData["phoneNumber"] as? String ?: return@mapNotNull null,
                    email = contactData["email"] as? String,
                    relationship = contactData["relationship"] as? String,
                    priority = (contactData["priority"] as? Number)?.toInt() ?: 0
                )
            } catch (e: Exception) {
                null
            }
        }
    }
    override suspend fun updateUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val userMap = user.toMap()
            usersCollection.document(user.id).update(userMap).await()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUser(id: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsersByRole(role: UserRole): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersCollection
                .whereEqualTo("role", role)
                .get()
                .await()

            val users = snapshot.documents.mapNotNull {
                it.toObject(User::class.java)
            }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUser(id: String): Flow<User?> = callbackFlow {
        val subscription = usersCollection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val user = snapshot.toObject(User::class.java)
                    trySend(user)
                } else {
                    trySend(null)
                }
            }

        awaitClose { subscription.remove() }
    }

    private suspend fun createCivilianUser(user: User) = withContext(Dispatchers.IO) {
        val civilianUser = CivilianUser(
            user = user,
            activeRequests = emptyList(),
            requestHistory = emptyList(),
            savedResources = emptyList()
        )
        firestore.collection("civilian_users")
            .document(user.id)
            .set(civilianUser)
            .await()
    }

    private suspend fun createResponseTeamUser(user: User) = withContext(Dispatchers.IO) {
        val responseTeamUser = ResponseTeamUser(
            user = user,
            primarySpecialization = ResponseSpecialization.RESCUE,
            secondarySpecializations = emptyList(),
            assignedTeamId = null,
            activeTaskIds = emptyList()
        )
        firestore.collection("response_team_users")
            .document(user.id)
            .set(responseTeamUser)
            .await()
    }

    private suspend fun createCoordinatorUser(user: User) = withContext(Dispatchers.IO) {
        val coordinatorUser = CoordinatorUser(
            user = user,
            managedTeams = emptyList(),
            managedRegions = emptyList(),
            activeOperations = emptyList()
        )
        firestore.collection("coordinator_users")
            .document(user.id)
            .set(coordinatorUser)
            .await()
    }

    private fun User.toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "email" to email,
        "displayName" to displayName,
        "phoneNumber" to phoneNumber,
        "role" to role,
        "lastKnownLocation" to lastKnownLocation,
        "isLocationSharingEnabled" to isLocationSharingEnabled,
        "createdAt" to createdAt,
        "lastActiveAt" to lastActiveAt,
        "emergencyContacts" to emergencyContacts,
        "deviceToken" to deviceToken
    )
}