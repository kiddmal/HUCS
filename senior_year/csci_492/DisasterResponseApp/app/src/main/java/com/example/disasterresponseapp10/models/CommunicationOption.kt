package com.example.disasterresponseapp10.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class CommunicationOption(
    val type: Type,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val descriptionRes: Int,
    val requiresAuth: Boolean = false
) {
    enum class Type {
        SAFETY_CHECK,
        EMERGENCY_CONTACTS,
        COMMUNITY_UPDATES,
        TEAM_CHAT
    }
}