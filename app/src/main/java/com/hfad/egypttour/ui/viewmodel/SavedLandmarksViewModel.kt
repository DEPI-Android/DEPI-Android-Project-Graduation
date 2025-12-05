package com.hfad.egypttour.ui.viewmodel

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

@HiltViewModel
class SavedLandmarksViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val landmarkRepository: LandmarkRepository
) : ViewModel() {

    private val _savedLandmarks = MutableStateFlow<List<LandMark>>(emptyList())
    val savedLandmarks = _savedLandmarks.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            val ids = userRepository.getUserFavorites()
            loadLandmarksByIds(ids)
        }
    }

    fun loadSaves() {
        viewModelScope.launch {
            _isLoading.value = true
            val ids = userRepository.getUserSaves()
            loadLandmarksByIds(ids)
        }
    }

    private suspend fun loadLandmarksByIds(ids: List<Int>) {
        if (ids.isEmpty()) {
            _savedLandmarks.value = emptyList()
            _isLoading.value = false
            return
        }

        // We fetch ALL landmarks then filter.
        // Note: For a larger app, we would add a specific getByIds() query to the repo.
        val allResult = landmarkRepository.getAllLandmarks()
        if (allResult is Result.Success) {
            val filtered = allResult.data.filter { ids.contains(it.id) }
            _savedLandmarks.value = filtered
        }
        _isLoading.value = false
    }
}