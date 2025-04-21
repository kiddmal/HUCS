package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Specific model for Civilian users with their request history
 */
@Parcelize
data class CivilianUser(
    val user: User,
    val activeRequests: List<String> = emptyList(), // Request IDs
    val requestHistory: List<String> = emptyList(),  // Historical Request IDs
    val savedResources: List<String> = emptyList(),  // Saved Resource IDs
    val safetyStatus: SafetyStatus = SafetyStatus.UNKNOWN
) : Parcelable

/**
 * Status enum for civilian safety
 */
enum class SafetyStatus {
    SAFE, NEED_HELP, UNKNOWN
}