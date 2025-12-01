//package com.hfad.egypttour.ui.viewmodel
//
//import android.util.Log
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.hfad.egypttour.data.model.Governorate
//import com.hfad.egypttour.data.model.LandMark
//import com.hfad.egypttour.data.model.Result
//import com.hfad.egypttour.data.repository.LandmarkRepository
//import com.hfad.egypttour.data.util.Constants
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
///**
// * UPDATED: Added Hilt injection and SavedStateHandle for process death recovery
// *
// * Key improvements:
// * 1. Survives process death (Android kills app in background)
// * 2. Restores selected governorate automatically
// * 3. Restores landmarks if available
// * 4. Uses Hilt for dependency injection
// */
//@HiltViewModel
//class LandmarkListViewModel @Inject constructor(
//    private val repository: LandmarkRepository,
//    private val savedStateHandle: SavedStateHandle  // Hilt automatically injects SavedStateHandle
//) : ViewModel() {
//
//    // SavedStateHandle keys
//    companion object {
//        private const val KEY_CURRENT_GOVERNORATE = "current_governorate"
//        private const val KEY_SCROLL_POSITION = "scroll_position"  // NEW: For scroll restoration
//    }
//
//    // ========== STATE MANAGEMENT ==========
//
//    private val _landmarksState = MutableStateFlow<Result<List<LandMark>>>(Result.Loading)
//    val landmarksState: StateFlow<Result<List<LandMark>>> = _landmarksState.asStateFlow()
//
//    /**
//     * UPDATED: Now uses SavedStateHandle to survive process death
//     */
//    private val _currentGovernorate = MutableStateFlow<Governorate?>(
//        savedStateHandle.get<Governorate>(KEY_CURRENT_GOVERNORATE)  // NEW: Restore from saved state
//    )
//    val currentGovernorate: StateFlow<Governorate?> = _currentGovernorate.asStateFlow()
//
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
//
//    private val _filteredLandmarks = MutableStateFlow<List<LandMark>>(emptyList())
//    val filteredLandmarks: StateFlow<List<LandMark>> = _filteredLandmarks.asStateFlow()
//
//    /**
//     * NEW: Scroll position saved to survive configuration changes
//     */
//    private val _scrollPosition = MutableStateFlow(
//        savedStateHandle.get<Int>(KEY_SCROLL_POSITION) ?: 0
//    )
//    val scrollPosition: StateFlow<Int> = _scrollPosition.asStateFlow()
//
//    // ========== INITIALIZATION ==========
//
//    /**
//     * NEW: Restore state on process recreation
//     */
//    init {
//        val savedGovernorate = savedStateHandle.get<Governorate>(KEY_CURRENT_GOVERNORATE)
//        if (savedGovernorate != null) {
//            Log.d(Constants.LOG_TAG, "Restoring landmarks for ${savedGovernorate.displayName}")
//            loadLandmarks(savedGovernorate)
//        }
//    }
//
//    // ========== PUBLIC API ==========
//
//    /**
//     * UPDATED: Now saves state to SavedStateHandle
//     */
//    fun loadLandmarks(governorate: Governorate, forceRefresh: Boolean = false) {
//        // Avoid redundant API calls
//        if (!forceRefresh && _currentGovernorate.value == governorate && _landmarksState.value is Result.Success) {
//            Log.d(Constants.LOG_TAG, "Landmarks already loaded for ${governorate.displayName}")
//            return
//        }
//
//        // NEW: Save to SavedStateHandle (survives process death)
//        savedStateHandle[KEY_CURRENT_GOVERNORATE] = governorate
//
//        _currentGovernorate.value = governorate
//        _landmarksState.value = Result.Loading
//
//        viewModelScope.launch {
//            Log.d(Constants.LOG_TAG, "Loading landmarks for ${governorate.displayName}")
//
//            val result = repository.getLandmarks(governorate)
//            _landmarksState.value = result
//
//            if (result is Result.Success) {
//                applySearchFilter(result.data)
//            }
//        }
//    }
//
//    fun retry() {
//        _currentGovernorate.value?.let { governorate ->
//            Log.d(Constants.LOG_TAG, "Retrying landmark load for ${governorate.displayName}")
//            loadLandmarks(governorate, forceRefresh = true)
//        }
//    }
//
//    fun updateSearchQuery(query: String) {
//        _searchQuery.value = query
//
//        val currentData = (_landmarksState.value as? Result.Success)?.data ?: emptyList()
//        applySearchFilter(currentData)
//    }
//
//    fun clearSearch() {
//        updateSearchQuery("")
//    }
//
//    /**
//     * NEW: Save scroll position to survive configuration changes
//     */
//    fun saveScrollPosition(position: Int) {
//        _scrollPosition.value = position
//        savedStateHandle[KEY_SCROLL_POSITION] = position
//    }
//
//    /**
//     * NEW: Reset scroll position (called when switching governorates)
//     */
//    fun resetScrollPosition() {
//        saveScrollPosition(0)
//    }
//
//    // ========== HELPER METHODS ==========
//
//    private fun applySearchFilter(landmarks: List<LandMark>) {
//        val query = _searchQuery.value
//
//        _filteredLandmarks.value = if (query.isBlank()) {
//            landmarks
//        } else {
//            landmarks.filter { landmark ->
//                landmark.name.contains(query, ignoreCase = true) ||
//                        landmark.description.contains(query, ignoreCase = true)
//            }
//        }
//
//        Log.d(Constants.LOG_TAG, "Search '$query' returned ${_filteredLandmarks.value.size} results")
//    }
//
//    // ========== CONVENIENCE PROPERTIES ==========
//
//    val isLoading: Boolean
//        get() = _landmarksState.value is Result.Loading
//
//    val isSuccess: Boolean
//        get() = _landmarksState.value is Result.Success
//
//    val isError: Boolean
//        get() = _landmarksState.value is Result.Error
//
//    val errorMessage: String?
//        get() = (_landmarksState.value as? Result.Error)?.massage
//
//    val landmarks: List<LandMark>
//        get() = (_landmarksState.value as? Result.Success)?.data ?: emptyList()
//
//    val landmarkCount: Int
//        get() = landmarks.size
//
//    val filteredLandmarkCount: Int
//        get() = _filteredLandmarks.value.size
//}


package com.hfad.egypttour.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.egypttour.data.api.RetrofitInstance
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.model.Result
import com.hfad.egypttour.data.repository.LandmarkRepository
import com.hfad.egypttour.data.util.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Hilt ViewModel
 */
@HiltViewModel
class LandmarkListViewModel @Inject constructor(
    private val repository: LandmarkRepository
) : ViewModel() {

    private val _landmarksState = MutableStateFlow<Result<List<LandMark>>>(Result.Loading)
    val landmarksState: StateFlow<Result<List<LandMark>>> = _landmarksState.asStateFlow()

    // NEW: State for the selected single landmark (for Detail Screen)
    private val _selectedLandmark = MutableStateFlow<Result<LandMark?>>(Result.Loading)
    val selectedLandmark: StateFlow<Result<LandMark?>> = _selectedLandmark.asStateFlow()

    private val _currentGovernorate = MutableStateFlow<Governorate?>(null)
    val currentGovernorate: StateFlow<Governorate?> = _currentGovernorate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredLandmarks = MutableStateFlow<List<LandMark>>(emptyList())
    val filteredLandmarks: StateFlow<List<LandMark>> = _filteredLandmarks.asStateFlow()

    private val _scrollPosition = MutableStateFlow(0)
    val scrollPosition: StateFlow<Int> = _scrollPosition.asStateFlow()

    val errorMessage: String?
        get() = (_landmarksState.value as? Result.Error)?.massage

    fun loadLandmarks(governorate: Governorate, forceRefresh: Boolean = false) {
        if (!forceRefresh && _currentGovernorate.value == governorate && _landmarksState.value is Result.Success) {
            Log.d(Constants.LOG_TAG, "Landmarks already loaded for ${governorate.displayName}")
            return
        }

        _currentGovernorate.value = governorate
        _landmarksState.value = Result.Loading

        viewModelScope.launch {
            Log.d(Constants.LOG_TAG, "Loading landmarks for ${governorate.displayName}")

            val result = repository.getLandmarks(governorate)
            _landmarksState.value = result

            if (result is Result.Success) {
                applySearchFilter(result.data)
            }
        }
    }


//    fun loadLandmarkDetails(id: Int) {
//        _selectedLandmark.value = Result.Loading
//
//        viewModelScope.launch {
//            val state = _landmarksState.value
//            Log.d("LandmarkVM", "Landmarks state: $state")
//
//            val existingLandmark = (state as? Result.Success)?.data?.find { it.id == id }
//            Log.d("LandmarkVM", "Existing landmark found: $existingLandmark")
//            if (existingLandmark != null) {
//                // Correct: assign LandMark? inside Result.Success
//                _selectedLandmark.value = Result.Success(existingLandmark)
//            } else {
//                // Error if not found
//                _selectedLandmark.value = Result.Error(Exception("Landmark not found"))
//            }
//        }
//    }

    fun loadLandmarkDetails(id: Int) {
        viewModelScope.launch {
            val currentState = _landmarksState.value
            val landmark = (currentState as? Result.Success)?.data?.find { it.id == id }
                ?: run {
                    // Not loaded yet — fetch from repository
                    val result = repository.getLandmarkById(id)
                    if (result is Result.Success) result.data else null
                }

            Log.d("LandmarkVM", "Existing landmark found: $landmark")
            _selectedLandmark.value = Result.Success(landmark)
        }
    }



    fun retry() {
        _currentGovernorate.value?.let { governorate ->
            Log.d(Constants.LOG_TAG, "Retrying landmark load for ${governorate.displayName}")
            loadLandmarks(governorate, forceRefresh = true)
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        val currentData = (_landmarksState.value as? Result.Success)?.data ?: emptyList()
        applySearchFilter(currentData)
    }

    fun clearSearch() {
        updateSearchQuery("")
    }

    fun saveScrollPosition(position: Int) {
        _scrollPosition.value = position
    }

    fun resetScrollPosition() {
        saveScrollPosition(0)
    }

    private fun applySearchFilter(landmarks: List<LandMark>) {
        val query = _searchQuery.value

        _filteredLandmarks.value = if (query.isBlank()) {
            landmarks
        } else {
            landmarks.filter { landmark ->
                landmark.name.contains(query, ignoreCase = true) ||
                        landmark.description.contains(query, ignoreCase = true)
            }
        }

        Log.d(Constants.LOG_TAG, "Search '$query' returned ${_filteredLandmarks.value.size} results")
    }
}