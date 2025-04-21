package com.example.disasterresponseapp10

data class UserProfile(
    val personalInfo: PersonalInfo,
    val contactInfo: ContactInfo,
    val emergencyContacts: List<EmergencyContact>,
    val skillsAndCertifications: List<String>,
    val languagePreferences: List<String>,
    val accessibilityNeeds: List<String>,
    val profilePhoto: String? = null  // Optional, can be a URL or file path
)