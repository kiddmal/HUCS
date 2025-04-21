package com.example.disasterresponseapp10.viewmodels

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.disasterresponseapp10.models.Resource

class UnifiedResourceViewModel : BaseViewModel() {
    private val _resourceState = MutableStateFlow<ResourceState>(ResourceState.Loading)
    val resourceState: StateFlow<ResourceState> = _resourceState.asStateFlow()

    private val _filteredResources = MutableStateFlow<List<Resource>>(emptyList())
    val filteredResources: StateFlow<List<Resource>> = _filteredResources.asStateFlow()

    private var currentFilters: Set<Resource.ResourceType> = emptySet()

    fun loadResources() = launchWithLoading {
        try {
            val resources = withContext(Dispatchers.IO) {
                fetchResources()
            }
            _resourceState.value = ResourceState.Success(resources)
            applyFilters(resources)
        } catch (e: Exception) {
            _resourceState.value = ResourceState.Error(e.message ?: "Failed to load resources")
        }
    }

    fun updateFilters(types: Set<Resource.ResourceType>) {
        currentFilters = types
        when (val state = resourceState.value) {
            is ResourceState.Success -> applyFilters(state.resources)
            else -> {} // Do nothing for other states
        }
    }

    private fun applyFilters(resources: List<Resource>) {
        viewModelScope.launch {
            _filteredResources.value = if (currentFilters.isEmpty()) {
                resources
            } else {
                resources.filter { it.type in currentFilters }
            }
        }
    }

    fun addResource(resource: Resource) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                withContext(Dispatchers.IO) {
                    // Simulate API call
                    delay(1000)
                    // TODO: Implement actual resource saving
                }
                loadResources() // Reload resources after adding
            } catch (e: Exception) {
                _resourceState.value = ResourceState.Error("Failed to add resource")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchResources(): List<Resource> = withContext(Dispatchers.IO) {
        // Simulate API call with sample data
        delay(1000)
        listOf(
            Resource(
                id = "1",
                name = "Emergency Shelter A",
                type = Resource.ResourceType.SHELTER,
                location = Resource.Location(
                    address = "123 Main St",
                    latitude = 37.7749,
                    longitude = -122.4194
                ),
                status = Resource.Status.OPEN,
                capacity = Resource.Capacity(
                    current = 45,
                    maximum = 100
                ),
                hours = "24/7",
                contact = "555-0123",
                distance = 5.5f
            )
            // Add more sample resources as needed
        )
    }

    sealed class ResourceState {
        object Loading : ResourceState()
        data class Success(val resources: List<Resource>) : ResourceState()
        data class Error(val message: String) : ResourceState()
    }
}