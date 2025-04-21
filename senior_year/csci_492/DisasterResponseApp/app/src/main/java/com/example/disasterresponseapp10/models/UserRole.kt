package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

enum class UserRole(val displayName: String, val firebaseRole: String) {
    CIVILIAN("Civilian", "civilian"),
    RESPONSE_TEAM("Response Team", "response_team"),
    COORDINATOR("Coordinator", "coordinator"),
    ADMINISTRATOR("Administrator", "administrator");

    companion object {
        fun fromFirebaseRole(role: String): UserRole {
            return entries.find { it.firebaseRole.equals(role, ignoreCase = true) } ?: CIVILIAN
        }
    }
}

@Parcelize
data class UserPermissions(
    val canAccessResponseTeam: Boolean = false,
    val canManageResources: Boolean = false,
    val canCoordinateTeams: Boolean = false,
    val canViewAnalytics: Boolean = false,
    val canAccessUserManagement: Boolean = false
) : Parcelable {

    companion object {
        fun fromFirestore(data: Map<String, Boolean>): UserPermissions {
            return UserPermissions(
                canAccessResponseTeam = data["access_response_team"] ?: false,
                canManageResources = data["manage_resources"] ?: false,
                canCoordinateTeams = data["coordinate_teams"] ?: false,
                canViewAnalytics = data["view_analytics"] ?: false,
                canAccessUserManagement = data["access_user_management"] ?: false
            )
        }

        fun toFirestore(permissions: UserPermissions): Map<String, Boolean> {
            return mapOf(
                "access_response_team" to permissions.canAccessResponseTeam,
                "manage_resources" to permissions.canManageResources,
                "coordinate_teams" to permissions.canCoordinateTeams,
                "view_analytics" to permissions.canViewAnalytics,
                "access_user_management" to permissions.canAccessUserManagement
            )
        }
    }
}