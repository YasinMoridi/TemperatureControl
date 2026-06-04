package com.yasinmoridi.temperaturecontrol.di

import com.yasinmoridi.temperaturecontrol.presentation.core.main.MainVM
import com.yasinmoridi.temperaturecontrol.presentation.feature.dashboard.DashboardVM
import com.yasinmoridi.temperaturecontrol.presentation.feature.devices.DevicesVM
import com.yasinmoridi.temperaturecontrol.presentation.feature.history.HistoryVM
import com.yasinmoridi.temperaturecontrol.presentation.feature.settings.SettingsVM
import com.yasinmoridi.temperaturecontrol.presentation.feature.splash.SplashVM
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Koin module for all ViewModel dependencies.
 * Uses viewModelOf for constructor-based dependency injection.
 */
val moduleViewModels = module {

    // Splash screen logic
    viewModelOf(::SplashVM)
    
    // Main dashboard and monitoring
    viewModelOf(::DashboardVM)
    
    // Bluetooth device management
    viewModelOf(::DevicesVM)
    
    // Historical data and logging
    viewModelOf(::HistoryVM)
    
    // App settings and preferences
    viewModelOf(::SettingsVM)
    
    // Root level application state
    viewModelOf(::MainVM)

}
