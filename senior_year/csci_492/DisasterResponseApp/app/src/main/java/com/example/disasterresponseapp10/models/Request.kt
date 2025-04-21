package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Model representing a request for assistance or resources
 */
@Parcelize
data class Request(
    val id: String,
    val type: RequestType,
    val status: RequestStatus,
    val priority: RequestPriority,
    val description: String,
    val location: UserLocation,
    // User relationships
    val requesterId: String,  // ID of the User who created the request
    val assignedTeamId: String? = null,  // ID of the ResponseTeam if assigned
    val assignedResponders: List<String> = emptyList(),  // IDs of assigned ResponseTeamUsers
    val coordinatorId: String? = null,  // ID of the Coordinator managing this request
    // Timestamps
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val assignedAt: Date? = null,
    val completedAt: Date? = null,
    // Additional details
    val requesterContact: String? = null,  // Contact information for the requester
    val estimatedDuration: Long? = null,  // Estimated duration in minutes
    val attachments: List<String> = emptyList(),  // URLs or references to attached files
    val tags: List<String> = emptyList(),  // For additional categorization
    val notes: List<RequestNote> = emptyList()  // Track request updates and communication
) : Parcelable

/**
 * Main categories of requests, aligned with ResponseSpecialization
 */
@Parcelize
enum class RequestType : Parcelable {
    RESCUE,    // Search and rescue, emergency response
    MEDICAL,   // Medical assistance and care
    TRANSPORT, // Evacuation and supply delivery
    REPAIR     // Infrastructure and utility restoration
}

/**
 * Status tracking for requests through their lifecycle
 */
@Parcelize
enum class RequestStatus : Parcelable {
    PENDING,       // Just created, not yet reviewed
    ASSIGNED,      // Assigned to team/responder
    IN_PROGRESS,   // Being actively worked on
    ON_HOLD,       // Temporarily paused
    COMPLETED,     // Work finished successfully 
    CANCELLED,     // Request cancelled or no longer needed
    TRANSFERRED    // Moved to different team/jurisdiction
}

/**
 * Priority levels for requests
 */
@Parcelize
enum class RequestPriority : Parcelable {
    CRITICAL,   // Immediate life-threatening situations
    HIGH,       // Urgent but not immediately life-threatening
    MEDIUM,     // Important but can be queued
    LOW         // Can be handled when resources available
}

/**
 * Notes for tracking updates, communication, and status changes
 */
@Parcelize
data class RequestNote(
    val id: String,
    val requestId: String,
    val userId: String,  // ID of the user who created the note
    val content: String,
    val type: NoteType,
    val timestamp: Date = Date()
) : Parcelable

/**
 * Types of notes that can be added to a request
 */
@Parcelize
enum class NoteType : Parcelable {
    STATUS_UPDATE,    // Changes in request status
    COMMUNICATION,    // Messages between involved parties
    ASSESSMENT,       // Evaluation notes
    INTERNAL,         // Notes visible only to response team
    SYSTEM           // Automated system updates
}

/**
 * Permissions for request operations based on user role
 */
object RequestPermissions {
    fun canCreate(role: UserRole): Boolean = true  // All users can create requests

    fun canAssign(role: UserRole): Boolean = when (role) {
        UserRole.COORDINATOR, UserRole.ADMINISTRATOR -> true
        else -> false
    }

    fun canUpdate(role: UserRole, request: Request, userId: String): Boolean = when (role) {
        UserRole.ADMINISTRATOR -> true
        UserRole.COORDINATOR -> true
        UserRole.RESPONSE_TEAM -> request.assignedResponders.contains(userId)
        UserRole.CIVILIAN -> request.requesterId == userId
    }

    fun canCancel(role: UserRole, request: Request, userId: String): Boolean = when (role) {
        UserRole.ADMINISTRATOR -> true
        UserRole.COORDINATOR -> true
        UserRole.CIVILIAN -> request.requesterId == userId
        else -> false
    }

    fun canAddNote(role: UserRole, request: Request, userId: String): Boolean =
        role != UserRole.CIVILIAN || request.requesterId == userId
}