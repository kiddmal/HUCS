package com.example.disasterresponseapp10.models

data class DashboardItem(
    val category: DashboardCategory,
    val isEnabled: Boolean = true,
    val requiresAuth: Boolean = false,
    val statusText: String? = null
)