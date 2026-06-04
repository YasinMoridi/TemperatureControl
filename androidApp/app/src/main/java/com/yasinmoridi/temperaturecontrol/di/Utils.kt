package com.yasinmoridi.temperaturecontrol.di

import com.yasinmoridi.temperaturecontrol.utils.PermissionManager
import com.yasinmoridi.temperaturecontrol.utils.network.ConnectivityObserver
import com.yasinmoridi.temperaturecontrol.utils.notification.NotifWorker
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

/**
 * Koin module for utility-related dependencies such as permissions, network monitoring, and workers.
 */
val utilsModule = module {
    // Provides the PermissionManager instance using androidContext
    single { PermissionManager(androidContext()) }
    
    // Provides a connectivity observer to track network status
    single { ConnectivityObserver(androidContext()) }
    
    // Defines a WorkManager worker for handling background notifications
    worker { NotifWorker(get(), get()) }
}
