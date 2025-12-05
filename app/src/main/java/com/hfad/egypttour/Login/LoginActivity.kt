package com.hfad.egypttour.Login

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.hfad.egypttour.ui.MainActivity
import com.hfad.egypttour.R
import com.hfad.egypttour.ui.theme.EgyptGoldDark
import com.hfad.egypttour.ui.theme.EgyptTourTheme
import kotlinx.coroutines.delay

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Check if user is already logged in
        val sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Only auto-login if both SharedPreferences and Firebase Auth agree
        if (isLoggedIn && currentUser != null) {
            // Both agree: user is logged in
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        } else if (isLoggedIn && currentUser == null) {
            // Clear stale session if Firebase Auth is null but SharedPreferences says logged in
            with(sharedPreferences.edit()) {
                putBoolean("isLoggedIn", false)
                apply()
            }
        }

        setContent {
            EgyptTourTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val loginBlue = Color(0xFF7F6C09)
    val fullText = "Welcome ! , \n ready \n to \n explore ?"
    val animatedText = remember { mutableStateOf("") }
    val bungeespiceRegular = FontFamily(Font(R.font.font_co))
    val welcomFont = FontFamily(Font(R.font.frijole_regular))
    val scrollState = rememberScrollState()

    val images = listOf(
        R.drawable.login_img,
        R.drawable.egyptian_photo,

    )
    val currentImageIndex = remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            fullText.forEachIndexed { index, _ ->
                delay(100)
                animatedText.value = fullText.substring(0, index + 1)
            }
            delay(1500) // Pause at the end
            animatedText.value = "" // Reset
            delay(500) // Pause before restart
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Switch image every 3 seconds
            currentImageIndex.value = (currentImageIndex.value + 1) % images.size
            //loop in three images
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFBAB9B3), Color(0xFFF6D575))
                )
            )
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(650.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(
                targetState = currentImageIndex.value,
                animationSpec = tween(1500)
            ) { imageIndex ->
                Image(
                    painter = painterResource(id = images[imageIndex]),
                    contentDescription = "Login header background",
                    modifier = Modifier.fillMaxSize(), // Fill the fixed-size container
                    contentScale = ContentScale.Crop // Crop the image to fill the space without distortion
                )
            }
            Text(
                text = animatedText.value,
                style = TextStyle(
                    fontFamily = welcomFont,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = EgyptGoldDark,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(top = 60.dp, bottom = 60.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))

                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { context.startActivity(Intent(context, SignInActivity::class.java)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = loginBlue)
                ) {
                    Text(text = "Sign In", color = Color.White, fontFamily = bungeespiceRegular)
                }

                OutlinedButton(
                    onClick = { context.startActivity(Intent(context, SignUpActivity::class.java)) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, loginBlue),
                ) {
                    Text(text = "Sign Up", color = loginBlue, fontFamily = bungeespiceRegular)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    EgyptTourTheme {
        LoginScreen()
    }
}
