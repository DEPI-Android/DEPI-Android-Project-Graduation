// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
/////////////////////////////////////
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.compose.compiler) apply false // Add this line

}// root build.gradle.kts

allprojects {
    configurations.all {
        resolutionStrategy {
            // FORCE the Kotlin Standard Lib to the stable version
            force("org.jetbrains.kotlin:kotlin-stdlib:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk7:2.0.21")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.0.21")
            force("org.jetbrains.kotlin:kotlin-reflect:2.0.21")

            // FORCE OkHttp to a stable version (4.12.0)
            // Your error logs show version 5.3.2 which is likely a SNAPSHOT/Alpha causing the issue
            force("com.squareup.okhttp3:okhttp:4.12.0")
            force("com.squareup.okhttp3:logging-interceptor:4.12.0")

            // FORCE Okio to stable
            force("com.squareup.okio:okio:3.9.0")
        }
    }
}