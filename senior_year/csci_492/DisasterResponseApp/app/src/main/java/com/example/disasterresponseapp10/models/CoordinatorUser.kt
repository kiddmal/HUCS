package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Model for Coordinator users with their management areas
 */
@Parcelize
data class CoordinatorUser(
    val user: User,
    val managedTeams: List<String> = emptyList(), // Team IDs
    val managedRegions: List<String> = emptyList(), // Region IDs
    val activeOperations: List<String> = emptyList() // Operation IDs
) : Parcelable