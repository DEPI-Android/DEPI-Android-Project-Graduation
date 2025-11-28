package com.hfad.egypttour

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Hilt Application class for dependency injection setup
 * This must be declared in AndroidManifest.xml as the android:name for <application>
 */
@HiltAndroidApp
class EgyptTourApp : Application()

