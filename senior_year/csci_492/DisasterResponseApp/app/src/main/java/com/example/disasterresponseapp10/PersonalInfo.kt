package com.example.disasterresponseapp10

data class PersonalInfo(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String,  // "YYYY-MM-DD" or you can use LocalDate for more flexibility
    val gender: String
)
