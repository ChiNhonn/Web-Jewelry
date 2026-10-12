package com.aura.jewelry

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main Android Application class for AURA Fine Jewelry.
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation,
 * creating an application-level dependency container.
 */
@HiltAndroidApp
class AuraApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Global initializations (e.g. Logging, Analytics, Crash reporting) can be placed here.
    }
}
