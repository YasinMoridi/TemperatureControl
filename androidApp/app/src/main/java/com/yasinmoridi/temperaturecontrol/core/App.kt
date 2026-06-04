package com.yasinmoridi.temperaturecontrol.core

import android.app.Application
import com.yasinmoridi.temperaturecontrol.di.*
import com.yasinmoridi.temperaturecontrol.utils.notification.createNotificationChannel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin

/**
 * Main Application class.
 * Initializes Koin for Dependency Injection and sets up system-level configurations like notification channels.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize notification channels for alerts
        createNotificationChannel()

        // Start Koin DI framework
        startKoin {
            // Provide Android context to Koin
            androidContext(this@App)
            
            // Integrate WorkManager with Koin
            workManagerFactory()
            
            // Load all dependency modules
            modules(
                dataStoreModule,
                networkModule,
                repositoryModule,
                moduleViewModels,
                utilsModule,
                bluetoothModule
            )
        }
    }
}
