package com.example.disasterresponseapp10.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.disasterresponseapp10.R

enum class DashboardCategory(
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val descriptionRes: Int
) {
    // Civilian Categories
    EMERGENCY_SERVICES(
        R.string.category_emergency_services,
        R.drawable.ic_emergency,
        R.string.category_emergency_description
    ),
    RESOURCES_AID(
        R.string.category_resources_aid,
        R.drawable.ic_resources,
        R.string.category_resources_description
    ),
    COMMUNICATION(
        R.string.category_communication,
        R.drawable.ic_communication,
        R.string.category_communication_description
    ),

    // response Team Categories
    RESPONSE_OPERATIONS(
        R.string.response_operations,
        R.drawable.ic_response,
        R.string.response_operations_description
    ),
    RESOURCES_MANAGEMENT(
        R.string.resource_management,
        R.drawable.ic_resource_management,
        R.string.resource_management_description
    ),
    TEAM_COMMUNICATION(
        R.string.team_chat,
        R.drawable.ic_team,
        R.string.team_chat_description
    ),

    // Coordinator Categories
    OPERATIONS_OVERVIEW(
        R.string.operations_overview,
        R.drawable.ic_overview,
        R.string.operations_overview_description
    ),
    TEAM_MANAGEMENT(
        R.string.team_management,
        R.drawable.ic_team_management,
        R.string.team_management_description
    ),
    RESOURCE_ALLOCATION(
        R.string.resource_allocation,
        R.drawable.ic_allocation,
        R.string.resource_allocation_description
    ),
    ANALYTICS(
        R.string.analytics,
        R.drawable.ic_analytics,
        R.string.analytics_description
    ),
    USER_MANAGEMENT(
    R.string.user_management,
    R.drawable.ic_profile,
    R.string.user_management_description
    )
}