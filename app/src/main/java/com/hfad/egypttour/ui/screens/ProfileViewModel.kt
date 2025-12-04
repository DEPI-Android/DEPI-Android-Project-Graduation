package com.hfad.egypttour.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.egypttour.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
 * Handles fetching user data from Firestore
 */
class ProfileViewModel : ViewModel() {

    // Firebase instances
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // UI State
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Fetch user data when ViewModel is created
        fetchUserData()
    }

    /**
     * Fetches user data from Firestore based on current user's UID
     */
    fun fetchUserData() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading

            try {
                // Get current user from Firebase Auth
                val currentUser = auth.currentUser

                if (currentUser == null) {
                    // No user is logged in
                    _uiState.value = ProfileUiState.NotAuthenticated
                    return@launch
                }

                // Get user UID
                val userId = currentUser.uid

                // Fetch user document from Firestore
                val documentSnapshot = db.collection("users")
                    .document(userId)
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    // Convert Firestore document to User object
                    val user = documentSnapshot.toObject(User::class.java)

                    if (user != null) {
                        _uiState.value = ProfileUiState.Success(user)
                    } else {
                        _uiState.value = ProfileUiState.Error("Failed to parse user data")
                    }
                } else {
                    _uiState.value = ProfileUiState.Error("User data not found")
                }

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(
                    e.message ?: "An error occurred while fetching user data"
                )
            }
        }
    }

    /**
     * Signs out the current user
     */
    fun signOut() {
        auth.signOut()
        _uiState.value = ProfileUiState.NotAuthenticated
    }
}