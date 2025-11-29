package com.hfad.egypttour

import android.app.Application
import com.hfad.egypttour.data.api.RetrofitInstance
import com.hfad.egypttour.data.api.WikiApiService
import com.hfad.egypttour.data.model.Governorate
import com.hfad.egypttour.data.repository.LandmarkRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Hilt Application class for dependency injection setup
 * This must be declared in AndroidManifest.xml as the android:name for <application>
 */
@HiltAndroidApp
class EgyptTourApp : Application()


