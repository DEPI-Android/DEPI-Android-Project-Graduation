package com.hfad.egypttour.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.egypttour.data.model.LandMark
import com.hfad.egypttour.data.repository.LandmarkRepository
import com.hfad.egypttour.data.repository.UserRepository
import com.hfad.egypttour.data.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for SavedLandmarksScreen
 * Loads user's favorite or saved landmarks using cache-first approach
 */
@HiltViewModel
class SavedLandmarksViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val landmarkRepository: LandmarkRepository
) : ViewModel() {

    companion object {
        private const val TAG = "SavedLandmarksVM"
    }

    private val _savedLandmarks = MutableStateFlow<List<LandMark>>(emptyList())
    val savedLandmarks = _savedLandmarks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    /**
     * Load favorites using cache-first approach
     * 1. Show cached immediately
     * 2. Sync with Firestore in background
     */
    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Step 1: Load from cache immediately (instant UI)
            val cachedIds = userRepository.getCachedFavorites()
            if (cachedIds.isNotEmpty()) {
                loadLandmarksByIds(cachedIds)
                _isLoading.value = false  // Stop loading indicator
                Log.d(TAG, "Loaded ${cachedIds.size} favorites from cache")
            }
            
            // Step 2: Sync with Firestore in background
            try {
                val freshIds = userRepository.syncFavoritesFromFirestore()
                if (freshIds != cachedIds) {
                    loadLandmarksByIds(freshIds)
                    Log.d(TAG, "Updated to ${freshIds.size} favorites from Firestore")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firestore sync failed: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    /**
     * Load saves using cache-first approach
     */
    fun loadSaves() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Step 1: Load from cache immediately
            val cachedIds = userRepository.getCachedSaves()
            if (cachedIds.isNotEmpty()) {
                loadLandmarksByIds(cachedIds)
                _isLoading.value = false
                Log.d(TAG, "Loaded ${cachedIds.size} saves from cache")
            }
            
            // Step 2: Sync with Firestore in background
            try {
                val freshIds = userRepository.syncSavesFromFirestore()
                if (freshIds != cachedIds) {
                    loadLandmarksByIds(freshIds)
                    Log.d(TAG, "Updated to ${freshIds.size} saves from Firestore")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Firestore sync failed: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    /**
     * Load full landmark objects by their IDs
     */
    private suspend fun loadLandmarksByIds(ids: List<Int>) {
        if (ids.isEmpty()) {
            _savedLandmarks.value = emptyList()
            return
        }

        // Fetch all landmarks then filter by IDs
        // TODO: For larger apps, add getByIds() query to repo for efficiency
        val allResult = landmarkRepository.getAllLandmarks()
        if (allResult is Result.Success) {
            val filtered = allResult.data.filter { ids.contains(it.id) }
            _savedLandmarks.value = filtered
        }
    }
}