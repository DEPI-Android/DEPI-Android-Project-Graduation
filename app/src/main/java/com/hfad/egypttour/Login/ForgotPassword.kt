package com.hfad.egypttour.Login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hfad.egypttour.R
import com.hfad.egypttour.ui.theme.EgyptGoldDark
import com.hfad.egypttour.ui.theme.EgyptTourTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ForgotPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EgyptTourTheme {
                ForgotPasswordScreen(onNavigateToLogin = {
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                })
            }
        }
    }
}

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class Success(val message: String) : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}

class ForgotPasswordViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState = _uiState.asStateFlow()

    var email by mutableStateOf("")

    fun sendPasswordResetEmail() {
        if (email.isBlank()) {
            _uiState.value = ForgotPasswordUiState.Error("Email cannot be empty.")
            return
        }
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            try {
                auth.sendPasswordResetEmail(email).await()
                Log.d("ForgotPassword", "Password reset email sent successfully.")
                _uiState.value = ForgotPasswordUiState.Success("Password reset email sent. Check your spam folder > noreply.")
            } catch (e: Exception) {
                Log.e("ForgotPassword", "Error sending password reset email", e)
                _uiState.value = ForgotPasswordUiState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }
     fun resetState() {
        _uiState.value = ForgotPasswordUiState.Idle
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: ForgotPasswordViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val forgot_Font = FontFamily(Font(R.font.frijole_regular))


    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF333333), Color(0xFF895100), Color(0xFFE4B643))
    )

    val cardBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.1f))
    )
    val buttonBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFB00020), Color(0xFFE4B643))
    )


    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ForgotPasswordUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                onNavigateToLogin()
                viewModel.resetState()
            }
            is ForgotPasswordUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(modifier = Modifier.background(cardBrush)) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Forgot Password",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgyptGoldDark,
                        fontFamily = forgot_Font
                    )
                    Text(
                        "Enter your email to receive a password reset link.",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    )

                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                         colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.7f),
                            focusedContainerColor = Color(0xFFE4B643),
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        )
                    )

                    Button(
                        onClick = { viewModel.sendPasswordResetEmail() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                             .background(buttonBrush, RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = uiState != ForgotPasswordUiState.Loading
                    ) {
                        if (uiState == ForgotPasswordUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("Reset Password", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
