package com.example.disasterresponseapp10.models

import java.util.Date

data class Notification(
    val id: String,
    val title: String,
    val message: String,
    val type: NotificationType,
    val timestamp: Date,
    val isRead: Boolean = false,
    val actionId: String? = null // For navigation/action purposes
)

enum class NotificationType {
    RESOURCE_UPDATE,     // For updates about shelters, supplies, etc.
    EMERGENCY_ALERT,     // For emergency broadcasts
    DELIVERY_UPDATE,     // For delivery status changes
    SYSTEM_MESSAGE,      // For general system notifications
    FAMILY_ALERT        // For family safety updates and notifications
}