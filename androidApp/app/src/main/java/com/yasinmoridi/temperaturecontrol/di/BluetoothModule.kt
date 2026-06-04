package com.yasinmoridi.temperaturecontrol.di

import com.yasinmoridi.temperaturecontrol.data.bluetooth.BluetoothManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module for Bluetooth-related dependencies.
 */
val bluetoothModule = module {
    // Provides a singleton instance of BluetoothManager using the application context
    single { BluetoothManager(androidContext()) }
}
