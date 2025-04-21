package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Model for Response Team members with their specializations and assignments
 */
@Parcelize
data class ResponseTeamUser(
    val user: User,
    val primarySpecialization: ResponseSpecialization, // Main response category
    val secondarySpecializations: List<ResponseSpecialization> = emptyList(), // Additional categories
    val assignedTeamId: String? = null,
    val activeTaskIds: List<String> = emptyList(),
    val status: ResponderStatus = ResponderStatus.OFF_DUTY,
    val skills: List<ResponderSkill> = emptyList(),
    val certifications: Map<String, Date> = emptyMap(), // Certification ID to expiration date
    val yearsOfExperience: Int = 0
) : Parcelable

/**
 * Main categories of Response Team specialization
 */
enum class ResponseSpecialization {
    RESCUE,    // Search and rescue, emergency response
    MEDICAL,   // Medical assistance and care
    TRANSPORT, // Evacuation and supply delivery
    REPAIR     // Infrastructure and utility restoration
}

/**
 * Status enum for responder availability
 */
enum class ResponderStatus {
    AVAILABLE, ON_TASK, OFF_DUTY, ON_BREAK
}

/**
 * Comprehensive list of skills for response team members
 * Grouped by primary response categories
 */
enum class ResponderSkill {
    // Rescue Skills
    SEARCH_AND_RESCUE,
    FIREFIGHTING,
    HAZMAT_HANDLING,
    EMERGENCY_RESPONSE,
    DISASTER_ASSESSMENT,

    // Medical Skills
    FIRST_AID,
    EMERGENCY_MEDICAL_CARE,
    TRIAGE,
    TRAUMA_CARE,
    CRISIS_COUNSELING,

    // Transport Skills
    EMERGENCY_DRIVING,
    EVACUATION_MANAGEMENT,
    SUPPLY_CHAIN_LOGISTICS,
    ROUTE_PLANNING,
    DELIVERY_COORDINATION,

    // Repair Skills
    STRUCTURAL_ASSESSMENT,
    ELECTRICAL_REPAIR,
    PLUMBING_REPAIR,
    DEBRIS_REMOVAL,
    TEMPORARY_SHELTER_SETUP
}