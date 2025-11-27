package com.hfad.egypttour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.egypttour.data.api.RetrofitInstance
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.data.repository.LandmarkRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LandmarkViewModel : ViewModel() {

    // 1. Initialize the Repository (using the Singleton Retrofit we made)
    private val repository = LandmarkRepository(RetrofitInstance.api)

    // 2. State to hold the list of landmarks
    private val _landmarks = MutableStateFlow<List<LandMark>>(emptyList())
    val landmarks: StateFlow<List<LandMark>> = _landmarks

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // 3. The Function to Fetch Data
    fun loadLandmarks(governorateId: String) {
        val governorate = Governorate.fromId(governorateId)
        if (governorate == null) {
            _error.value = "Invalid City ID"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null // Clear previous errors

            // CALL THE REPOSITORY
            val result = repository.getLandmarks(governorate)

            when (result) {
                is Result.Success -> {
                    _landmarks.value = result.data
                }
                is Result.Error -> {
                    _error.value = (result.message ?: "Unknown Error") as String?
                }
                is Result.Loading -> {} // No-op
            }
            _isLoading.value = false
        }
    }
}