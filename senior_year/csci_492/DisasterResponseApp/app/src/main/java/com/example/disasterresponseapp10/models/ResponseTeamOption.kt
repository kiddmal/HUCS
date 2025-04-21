package com.example.disasterresponseapp10.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class ResponseTeamOption(
    val type: Type,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val descriptionRes: Int,
    val requiresAuth: Boolean = true  // Added this field with default value true
) {
    enum class Type {
        response_OPS,
        RESOURCE_MGMT,
        ROUTE_PLANNING,
        TEAM_COORD
    }
}