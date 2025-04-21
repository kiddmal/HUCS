package com.example.disasterresponseapp10

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository()
    val userProfileLiveData = MutableLiveData<UserProfile>()

    // Load profile data from repository
    fun loadUserProfile() {
        viewModelScope.launch {
            try {
                val profile = repository.fetchUserProfile()  // Replace with actual data fetching logic
                userProfileLiveData.postValue(profile)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error loading profile: ${e.message}")
            }
        }
    }

    // Save updated profile data (implement your save logic here)
    fun updateProfile(updatedProfile: UserProfile) {
        viewModelScope.launch {
            try {
                repository.saveUserProfile(updatedProfile)
                userProfileLiveData.postValue(updatedProfile)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile: ${e.message}")
            }
        }
    }
}
