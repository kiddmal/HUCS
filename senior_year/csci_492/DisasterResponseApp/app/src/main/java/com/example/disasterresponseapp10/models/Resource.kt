package com.example.disasterresponseapp10.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Resource(
    val id: String,
    val name: String,
    val type: ResourceType,
    val location: Location,
    val status: Status,
    val capacity: Capacity?,
    val hours: String,
    val distance: Float,
    val contact: String
) : Parcelable {
    @Parcelize
    enum class ResourceType : Parcelable {
        SHELTER, FOOD, MEDICAL, WATER
    }

    @Parcelize
    enum class Status : Parcelable {
        OPEN, CLOSED, FULL
    }

    @Parcelize
    data class Location(
        val address: String,
        val latitude: Double,
        val longitude: Double
    ) : Parcelable

    @Parcelize
    data class Capacity(
        val current: Int,
        val maximum: Int
    ) : Parcelable
}