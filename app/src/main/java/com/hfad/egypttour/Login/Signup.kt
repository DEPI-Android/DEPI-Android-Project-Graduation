package com.hfad.egypttour.Login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import com.airbnb.lottie.compose.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.hfad.egypttour.R
import com.hfad.egypttour.ui.theme.EgyptTourTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

/**
 * SignUpActivity - Entry point for the signup screen
 */
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EgyptTourTheme {
                SignUpScreen(onNavigateToLogin = {
                    startActivity(Intent(this, SignInActivity::class.java))
                    finish()
                })
            }
        }
    }
}

/**
 * UI State for signup process
 */
sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    data class Success(val message: String) : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}

/**
 * ViewModel for signup - handles Firebase Auth and Firestore operations
 */
class SignUpViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState = _uiState.asStateFlow()

    var username by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)

    /**
     * Validates password strength
     */
    private fun isPasswordStrong(password: String): Pair<Boolean, String> {
        return when {
            password.length < 8 -> Pair(false, "Password must be at least 8 characters")
            !password.any { it.isUpperCase() } -> Pair(false, "Password must contain uppercase letter")
            !password.any { it.isLowerCase() } -> Pair(false, "Password must contain lowercase letter")
            !password.any { it.isDigit() } -> Pair(false, "Password must contain a number")
            else -> Pair(true, "")
        }
    }

    /**
     * Converts Firebase exceptions to user-friendly messages
     */
    private fun getReadableError(exception: Exception): String {
        return when (exception) {
            is FirebaseAuthUserCollisionException -> "An account already exists with this email address."
            else -> when {
                exception.message?.contains("no user record") == true ->
                    "No account found with this email"
                exception.message?.contains("password is invalid") == true ->
                    "Incorrect password"
                exception.message?.contains("email address is badly formatted") == true ->
                    "Invalid email format"
                exception.message?.contains("network error") == true ->
                    "No internet connection"
                exception.message?.contains("too many requests") == true ->
                    "Too many attempts. Please try again later"
                else -> exception.message ?: "An error occurred"
            }
        }
    }

    /**
     * Sign up with email and password
     * Creates Firebase Auth user, saves to Firestore, and caches locally
     */
    fun signUp(context: Context) {
        // Validate all fields are filled
        if (username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            _uiState.value = SignUpUiState.Error("Please fill all fields.")
            return
        }

        // Validate email format
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = SignUpUiState.Error("Please enter a valid email.")
            return
        }

        // Validate password strength
        val (isStrong, errorMessage) = isPasswordStrong(password)
        if (!isStrong) {
            _uiState.value = SignUpUiState.Error(errorMessage)
            return
        }

        // Validate passwords match
        if (password != confirmPassword) {
            _uiState.value = SignUpUiState.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            val originalUsername = username
            val originalEmail = email

            try {
                // Step 1: Create Firebase Auth user with timeout
                val result = withTimeoutOrNull(6000) {
                    auth.createUserWithEmailAndPassword(originalEmail, password).await()
                }

                if (result == null) {
                    _uiState.value = SignUpUiState.Error("Request timed out while creating account. Please try again.")
                    username = originalUsername
                    email = originalEmail
                    password = ""
                    confirmPassword = ""
                    return@launch
                }

                val user = result.user ?: throw Exception("User creation failed, user is null.")
                val userId = user.uid

                // Step 2: Save user profile to Firestore
                val userMap = hashMapOf(
                    "username" to originalUsername,
                    "email" to originalEmail,
                    "createdAt" to Timestamp.now()
                )

                val firestoreResult = withTimeoutOrNull(10000) {
                    db.collection("users").document(userId).set(userMap).await()
                }

                // Step 3: Cache profile locally regardless of Firestore result
                val sharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putString("username", originalUsername)
                    .putString("email", originalEmail)
                    .apply()

                if (firestoreResult == null) {
                    // Account created but Firestore timed out - still a success!
                    _uiState.value = SignUpUiState.Success("Account created! Please sign in to continue.")
                    clearFields()
                    return@launch
                }

                // Step 4: Send email verification
                val emailVerificationResult = withTimeoutOrNull(10000) {
                    user.sendEmailVerification().await()
                }

                if (emailVerificationResult == null) {
                    _uiState.value = SignUpUiState.Success("Sign up successful, but failed to send verification email. Please try resending verification later.")
                } else {
                    _uiState.value = SignUpUiState.Success("Sign up successful! A verification link has been sent. Please check your inbox and spam folder.")
                }
                clearFields()

            } catch (e: Exception) {
                _uiState.value = SignUpUiState.Error(getReadableError(e))
                username = originalUsername
                email = originalEmail
                password = ""
                confirmPassword = ""
            }
        }
    }

    private fun clearFields() {
        username = ""
        email = ""
        password = ""
        confirmPassword = ""
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }
}

/**
 * Custom text field colors for the signup form
 */
@Composable
fun customTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White.copy(alpha = 0.7f),
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        focusedBorderColor = Color.White,
        unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
        cursorColor = Color.White
    )
}

/**
 * Signup screen composable
 */
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()
    val signup_Font = FontFamily(Font(R.font.frijole_regular))
    val scrollState = rememberScrollState()
    val showSuccessDialog = remember { mutableStateOf<String?>(null) }
    val showErrorDialog = remember { mutableStateOf<String?>(null) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFFA07503), Color(0xFF8E8E8E), Color(0xFFF9C58D))
    )

    val cardBrush = Brush.linearGradient(
        colors = listOf(Color.White.copy(alpha = 0.2f), Color.White.copy(alpha = 0.1f))
    )

    val buttonBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFA19004), Color(0xFFF8D8B6))
    )

    val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loadin_lo))
    val lottieProgress by animateLottieCompositionAsState(
        lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    // Handle UI state changes
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is SignUpUiState.Success -> {
                showSuccessDialog.value = state.message
            }
            is SignUpUiState.Error -> {
                showErrorDialog.value = state.message
            }
            else -> Unit
        }
    }

    // Success Dialog
    showSuccessDialog.value?.let { message ->
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog.value = null
                viewModel.resetState()
                onNavigateToLogin()
            },
            title = { Text("Success", fontWeight = FontWeight.Bold) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog.value = null
                    viewModel.resetState()
                    onNavigateToLogin()
                }) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    showErrorDialog.value?.let { message ->
        AlertDialog(
            onDismissRequest = {
                showErrorDialog.value = null
                viewModel.resetState()
            },
            title = { Text("Error", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { Text(message) },
            confirmButton = {
                TextButton(onClick = {
                    showErrorDialog.value = null
                    viewModel.resetState()
                }) {
                    Text("OK")
                }
            }
        )
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
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // User icon
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Icon",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.8f))
                            .padding(16.dp),
                        tint = Color(0xFFFFC107)
                    )

                    // Title
                    Text(
                        "Sign Up",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = signup_Font
                    )

                    // Username field
                    OutlinedTextField(
                        value = viewModel.username,
                        onValueChange = { viewModel.username = it },
                        label = { Text("Username") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = customTextFieldColors()
                    )

                    // Email field
                    OutlinedTextField(
                        value = viewModel.email,
                        onValueChange = { viewModel.email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        colors = customTextFieldColors()
                    )

                    // Password field
                    OutlinedTextField(
                        value = viewModel.password,
                        onValueChange = { viewModel.password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val image = if (viewModel.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { viewModel.passwordVisible = !viewModel.passwordVisible }) {
                                Icon(imageVector = image, null)
                            }
                        },
                        visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = customTextFieldColors()
                    )

                    // Confirm Password field
                    OutlinedTextField(
                        value = viewModel.confirmPassword,
                        onValueChange = { viewModel.confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            val image = if (viewModel.confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { viewModel.confirmPasswordVisible = !viewModel.confirmPasswordVisible }) {
                                Icon(imageVector = image, null)
                            }
                        },
                        visualTransformation = if (viewModel.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            focusManager.clearFocus()
                            viewModel.signUp(context)
                        }),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(50),
                        colors = customTextFieldColors()
                    )

                    // Sign Up button
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.signUp(context)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .background(buttonBrush, RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        enabled = uiState != SignUpUiState.Loading
                    ) {
                        if (uiState == SignUpUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text("SIGN UP", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    // Navigate to Sign In
                    Row {
                        Text("Already have an account? ", color = Color.White.copy(alpha = 0.8f))
                        TextButton(onClick = {
                            context.startActivity(Intent(context, SignInActivity::class.java))
                        }) {
                            Text("Sign in!", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Loading overlay
        if (uiState == SignUpUiState.Loading) {
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
fun SignUpScreenPreview() {
    EgyptTourTheme {
        SignUpScreen(onNavigateToLogin = {})
    }
}
