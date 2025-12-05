package com.hfad.egypttour.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.*
import com.google.firebase.auth.FirebaseAuth
import com.hfad.egypttour.ui.MainActivity
import com.hfad.egypttour.R
import com.hfad.egypttour.ui.theme.EgyptGoldDark
import com.hfad.egypttour.ui.theme.EgyptTourTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EgyptTourTheme {
                SignInScreen(onNavigateToMain = {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }, onNavigateToSignUp = {
                    startActivity(
                        Intent(
                            this, SignUpActivity::class.java
                        )
                    )
                }, onNavigateToForgotPassword = {
                    startActivity(
                        Intent(
                            this, ForgotPasswordActivity::class.java
                        )
                    )
                })
            }
        }
    }
}

sealed class SignInUiState {
    object Idle : SignInUiState()
    object Loading : SignInUiState()
    data class Success(val message: String) : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}

class SignInViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: com.google.firebase.firestore.FirebaseFirestore = 
        com.google.firebase.firestore.FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState = _uiState.asStateFlow()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var rememberMe by mutableStateOf(false)

    /**
     * Sign in with email and password, then fetch and cache user profile
     */
    fun signIn(context: Context) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = SignInUiState.Error("Please fill all fields.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                
                val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                
                // Save login session if "Remember me" is checked
                if (rememberMe) {
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                }
                
                // Fetch and cache user profile from Firestore
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    try {
                        val doc = db.collection("users").document(userId).get().await()
                        if (doc.exists()) {
                            val username = doc.getString("username") ?: ""
                            val userEmail = doc.getString("email") ?: email
                            // Cache profile locally
                            sharedPreferences.edit()
                                .putString("username", username)
                                .putString("email", userEmail)
                                .apply()
                        }
                    } catch (e: Exception) {
                        // If Firestore fetch fails, still allow login
                        // Profile will show email from Firebase Auth as fallback
                        sharedPreferences.edit()
                            .putString("email", email)
                            .apply()
                    }
                }
                
                _uiState.value = SignInUiState.Success("Login Successful")
            } catch (e: Exception) {
                _uiState.value = SignInUiState.Error(e.message ?: "An unknown error occurred.")
            }
        }
    }

    fun resetState() {
        _uiState.value = SignInUiState.Idle
    }
}

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()
    val signin_Font = FontFamily(Font(R.font.frijole_regular))
    val scrollState = rememberScrollState()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF333333), Color(0xFF895100), Color(0xFFE4B643))
    )

    val cardBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.1f))
    )

    val buttonBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFB00020), Color(0xFFE4B643))
    )

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loadin_lo))
    val lottieProgress by animateLottieCompositionAsState(
        lottieComposition, iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is SignInUiState.Success -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                onNavigateToMain()
                viewModel.resetState()
            }

            is SignInUiState.Error -> {
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
            modifier = Modifier.fillMaxWidth(0.9f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(modifier = Modifier.background(cardBrush)) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.8f))
                            .padding(16.dp),
                        tint = Color(0xFFE4B643)
                    )

                    Text(
                        "Sign In",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EgyptGoldDark,
                        fontFamily = signin_Font
                    )

                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email, imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = {
                            focusManager.moveFocus(
                                FocusDirection.Down
                            )
                        }),
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

                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val image =
                                if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = {
                                viewModel.passwordVisible = !viewModel.passwordVisible
                            }) {
                                Icon(imageVector = image, null)
                            }
                        },
                        visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password, imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.7f),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                            cursorColor = Color.White
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                true,

                                onClick = { viewModel.rememberMe= !viewModel.rememberMe }
                            )
                        ) {
                            Checkbox(
                                checked = viewModel.rememberMe,
                                onCheckedChange = { viewModel.rememberMe = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White, checkmarkColor = Color(0xFFFF9800)
                                )
                            )
                            Text(
                                "Remember me", color = Color.White.copy(alpha = 0.8f),

                                )
                        }
                        TextButton(onClick = onNavigateToForgotPassword) {
                            Text("Forgot password?", color = Color.White.copy(alpha = 0.8f))
                        }
                    }

                    Button(
                        onClick = { viewModel.signIn(context) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(buttonBrush, RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = uiState != SignInUiState.Loading
                    ) {
                        Text(
                            "LOGIN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Row {
                        Text("Don\'t have account? ", color = Color.White.copy(alpha = 0.8f))
                        TextButton(onClick = onNavigateToSignUp) {
                            Text(
                                "Sign up!",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
            }
        }
        if (uiState == SignInUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                LottieAnimation(
                    composition = lottieComposition,
                    progress = { lottieProgress },
                    modifier = Modifier.size(200.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    EgyptTourTheme {
        SignInScreen(
            onNavigateToMain = {},
            onNavigateToSignUp = {},
            onNavigateToForgotPassword = {})
    }
}
