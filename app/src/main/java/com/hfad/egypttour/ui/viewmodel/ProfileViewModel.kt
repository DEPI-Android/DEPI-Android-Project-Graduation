package com.hfad.egypttour.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hfad.egypttour.data.model.User
import com.hfad.egypttour.data.repository.AuthRepository
import com.hfad.egypttour.data.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI State for Profile Screen
 */
sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val user: User) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
    object NotAuthenticated : ProfileUiState()
}

/**
 * ViewModel for Profile Screen
 * Uses Hilt for dependency injection
 * Loads user data from cache first (instant), falls back to Firestore if needed
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Logout event (single-time event for navigation)
    private val _logoutEvent = MutableStateFlow(false)
    val logoutEvent: StateFlow<Boolean> = _logoutEvent.asStateFlow()

    // Loading state for "logout from all devices"
    private val _isLoggingOutFromAllDevices = MutableStateFlow(false)
    val isLoggingOutFromAllDevices: StateFlow<Boolean> = _isLoggingOutFromAllDevices.asStateFlow()

    init {
        // Don't fetch automatically - let the UI trigger it when ready
    }

    /**
     * Fetches user data - tries cache first (instant), falls back to Firestore
     */
    fun fetchUserData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            // Check if user is authenticated
            if (!authRepository.isAuthenticated()) {
                _uiState.value = ProfileUiState.NotAuthenticated
                return@launch
            }

            // FIRST: Try to load from cache (instant!)
            val cachedUsername = sessionManager.getCachedUsername()
            val cachedEmail = sessionManager.getCachedEmail()

            if (!cachedUsername.isNullOrEmpty() || !cachedEmail.isNullOrEmpty()) {
                // We have cached data - display it immediately
                val user = User(
                    username = cachedUsername ?: "",
                    email = cachedEmail ?: ""
                )
                _uiState.value = ProfileUiState.Success(user)
                return@launch
            }

            // FALLBACK: If no cache, try Firestore (for existing users who haven't logged in since update)
            val userId = authRepository.getCurrentUserId()
            if (userId == null) {
                _uiState.value = ProfileUiState.NotAuthenticated
                return@launch
            }

            val result = authRepository.getUserData(userId)
            
            result.onSuccess { user ->
                // Cache for next time
                sessionManager.saveUserProfile(user.username, user.email)
                _uiState.value = ProfileUiState.Success(user)
            }.onFailure { exception ->
                _uiState.value = ProfileUiState.Error(
                    exception.message ?: "An error occurred while fetching user data"
                )
            }
        }
    }

    /**
     * Sign out from THIS DEVICE ONLY
     * Clears both Firebase Auth and SharedPreferences session
     */
    fun signOut() {
        authRepository.signOut()
        _uiState.value = ProfileUiState.NotAuthenticated
        _logoutEvent.value = true
    }

    /**
     * Sign out from ALL DEVICES
     * Revokes refresh tokens globally via Cloud Function
     */
    fun signOutFromAllDevices() {
        viewModelScope.launch {
            _isLoggingOutFromAllDevices.value = true

            val result = authRepository.signOutFromAllDevices()

            result.onSuccess {
                _uiState.value = ProfileUiState.NotAuthenticated
                _logoutEvent.value = true
            }.onFailure { exception ->
                // Show error but still logout locally
                _uiState.value = ProfileUiState.Error(
                    "Failed to logout from all devices: ${exception.message}. Logged out locally."
                )
                authRepository.signOut()
                _logoutEvent.value = true
            }

            _isLoggingOutFromAllDevices.value = false
        }
    }

    /**
     * Reset logout event after it's been handled
     */
    fun onLogoutEventHandled() {
        _logoutEvent.value = false
    }
}