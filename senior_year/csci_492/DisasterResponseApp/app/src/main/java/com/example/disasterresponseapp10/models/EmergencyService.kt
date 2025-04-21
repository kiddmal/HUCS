package com.example.disasterresponseapp10.models

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class EmergencyService(
    val type: Type,
    @StringRes val titleRes: Int,
    @DrawableRes val iconRes: Int,
    @StringRes val descriptionRes: Int
) {
    enum class Type {
    }
}
