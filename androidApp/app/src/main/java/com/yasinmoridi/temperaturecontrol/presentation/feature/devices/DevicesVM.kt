package com.yasinmoridi.temperaturecontrol.presentation.feature.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.data.bluetooth.BluetoothManager
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Data model for a Bluetooth device displayed in the list.
 */
data class Device(
    val name: String,
    val signal: String,
    val isConnected: Boolean,
    val type: DeviceType,
    val address: String = ""
)

/**
 * Categorization of devices to show appropriate icons.
 */
enum class DeviceType {
    BLUETOOTH, SMARTPHONE, WATCH
}

class DevicesVM(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    
    // UI state for Bluetooth radio power status
    private val _isBluetoothEnabled = MutableStateFlow(true)
    val isBluetoothEnabled = _isBluetoothEnabled.asStateFlow()

    // UI state for showing the scanning/loading indicator
    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    // Core hardware node (the ATmega chip) - defined as a static member for this project
    private val atmegaNode = Device(UiStrings.DEVICE_NAME, UiStrings.MOCK_SIGNAL, true, DeviceType.BLUETOOTH)

    /**
     * Combined flow of discovered devices from the manager and the scanning state.
     * Ensures the core hardware node is always visible.
     */
    val devices = bluetoothManager.discoveredDevices
        .combine(_isScanning) { discovered, scanning ->
            val list = mutableListOf<Device>()
            
            // Always include the primary controller node at the top
            list.add(atmegaNode)
            
            // Map real Bluetooth scan results into our UI model
            discovered.forEach { 
                if (it.name != UiStrings.DEVICE_NAME) { // Avoid duplicate entries for the main node
                    list.add(Device(
                        name = it.name,
                        signal = "${it.rssi} ${UiStrings.SIGNAL_UNIT}",
                        isConnected = false,
                        type = DeviceType.BLUETOOTH,
                        address = it.address
                    ))
                }
            }
            list
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(atmegaNode))

    /**
     * Simulates toggling the Bluetooth hardware.
     */
    fun toggleBluetooth(enabled: Boolean) {
        _isBluetoothEnabled.value = enabled
    }

    /**
     * Triggers a new Bluetooth discovery scan.
     */
    fun startScan() {
        if (_isScanning.value) return
        
        viewModelScope.launch {
            _isScanning.value = true
            bluetoothManager.startDiscovery()
            
            // Discovery period is handled by the manager (usually 10s)
            kotlinx.coroutines.delay(10000)
            _isScanning.value = false
        }
    }

    /**
     * Terminates connection with the specified device.
     */
    fun disconnectDevice(device: Device) {
        if (device.name == UiStrings.DEVICE_NAME) {
            bluetoothManager.disconnect()
        }
    }

    /**
     * Initiates connection with the specified device.
     */
    fun connectDevice(device: Device) {
        if (device.name == UiStrings.DEVICE_NAME) {
            bluetoothManager.startConnection()
        }
        // Connection logic for other discovered BLE devices can be implemented here
    }
}
