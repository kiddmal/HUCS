
package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Base User class containing common properties for all user types.
 * Implements Parcelable for safe navigation argument passing.
 */
@Parcelize
data class User(
    val id: String,  // Firebase UID
    val email: String,
    val displayName: String,
    val phoneNumber: String? = null,
    val role: UserRole,
    val lastKnownLocation: UserLocation? = null,
    val isLocationSharingEnabled: Boolean = false,
    val createdAt: Date = Date(),
    val lastActiveAt: Date = Date(),
    val emergencyContacts: List<EmergencyContact> = emptyList(),
    val deviceToken: String? = null // For FCM push notifications
) : Parcelable

/**
 * Location data class for tracking user positions
 * Uses a double array for GeoFire compatibility
 */
@Parcelize
data class UserLocation(
    val coordinates: DoubleArray,  // [latitude, longitude] for GeoFire
    val address: String? = null,
    val timestamp: Date = Date(),
    val accuracy: Float? = null,
    val isSharing: Boolean = false
) : Parcelable {
    // Override equals and hashCode since we're using DoubleArray
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserLocation

        if (!coordinates.contentEquals(other.coordinates)) return false
        if (address != other.address) return false
        if (timestamp != other.timestamp) return false
        if (accuracy != other.accuracy) return false
        if (isSharing != other.isSharing) return false

        return true
    }

    override fun hashCode(): Int {
        var result = coordinates.contentHashCode()
        result = 31 * result + (address?.hashCode() ?: 0)
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (accuracy?.hashCode() ?: 0)
        result = 31 * result + isSharing.hashCode()
        return result
    }

    companion object {
        // Helper functions for working with coordinates
        fun getLatitude(coordinates: DoubleArray): Double = coordinates[0]
        fun getLongitude(coordinates: DoubleArray): Double = coordinates[1]

        // Create UserLocation from latitude and longitude
        fun fromLatLng(latitude: Double, longitude: Double, address: String? = null): UserLocation {
            return UserLocation(
                coordinates = doubleArrayOf(latitude, longitude),
                address = address
            )
        }
    }
}

/**
 * Emergency contact information for users
 */
@Parcelize
data class EmergencyContact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val email: String? = null,
    val relationship: String? = null,
    val priority: Int = 0 // For ordering contacts
) : Parcelable