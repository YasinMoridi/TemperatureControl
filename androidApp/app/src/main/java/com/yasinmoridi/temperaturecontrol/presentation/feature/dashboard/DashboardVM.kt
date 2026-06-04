package com.yasinmoridi.temperaturecontrol.presentation.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.data.bluetooth.BluetoothManager
import com.yasinmoridi.temperaturecontrol.data.dataStore.DataStoreRepository
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import com.yasinmoridi.temperaturecontrol.utils.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the Dashboard screen, responsible for handling real-time data from the 
 * temperature control system and managing user interactions.
 */
class DashboardVM(
    private val bluetoothManager: BluetoothManager,
    private val repository: DataStoreRepository
) : ViewModel() {
    
    // Current temperature reading from the sensor
    private val _temperature = MutableStateFlow(0f)
    val temperature = _temperature.asStateFlow()

    // Recent temperature readings for chart visualization
    private val _tempHistory = MutableStateFlow<List<Float>>(emptyList())
    val tempHistory = _tempHistory.asStateFlow()

    // Current state of the cooling fan
    private val _isFanOn = MutableStateFlow(false)
    val isFanOn = _isFanOn.asStateFlow()

    // User-defined temperature threshold for automatic fan control
    private val _threshold = MutableStateFlow(30)
    val threshold = _threshold.asStateFlow()

    // Total runtime of the fan during the current session
    private val _fanRuntime = MutableStateFlow(UiStrings.DEFAULT_RUNTIME)
    val fanRuntime = _fanRuntime.asStateFlow()

    // Current connection status of the Bluetooth system
    private val _systemStatus = MutableStateFlow(UiStrings.STATUS_DISCONNECTED)
    val systemStatus = _systemStatus.asStateFlow()

    // Keeps track of the active Bluetooth connection stream
    private var connectionJob: Job? = null

    init {
        // Start observing persistent settings
        observeThreshold()
    }

    /**
     * Observes the threshold value from DataStore and synchronizes it with the hardware.
     */
    private fun observeThreshold() {
        viewModelScope.launch {
            repository.observeInt(Constants.THRESHOLD_KEY).collectLatest { value ->
                val newThreshold = value ?: 30
                _threshold.value = newThreshold
                // If connected, sync the new threshold to the remote device
                if (systemStatus.value == UiStrings.STATUS_CONNECTED) {
                    bluetoothManager.sendThreshold(newThreshold)
                }
            }
        }
    }

    /**
     * Initiates a connection to the temperature control system and starts receiving real-time data.
     */
    fun connectToSystem() {
        connectionJob?.cancel()
        connectionJob = viewModelScope.launch {
            _systemStatus.value = UiStrings.STATUS_CONNECTING
            bluetoothManager.connectAndReceive().collect { result ->
                result.onSuccess { data ->
                    // Set status to connected and sync threshold on first successful packet
                    if (_systemStatus.value != UiStrings.STATUS_CONNECTED) {
                        _systemStatus.value = UiStrings.STATUS_CONNECTED
                        bluetoothManager.sendThreshold(_threshold.value)
                    }
                    
                    // Update current state
                    _temperature.value = data.temperature
                    _isFanOn.value = data.fanOn
                    
                    // Maintain a rolling history of the last 20 temperature points
                    val currentHistory = _tempHistory.value.toMutableList()
                    currentHistory.add(data.temperature)
                    if (currentHistory.size > 20) {
                        currentHistory.removeAt(0)
                    }
                    _tempHistory.value = currentHistory
                    
                }.onFailure {
                    _systemStatus.value = "${UiStrings.ERROR_PREFIX}${it.message}"
                }
            }
        }
    }

    /**
     * Terminate the active Bluetooth connection.
     */
    fun disconnect() {
        connectionJob?.cancel()
        bluetoothManager.disconnect()
        _systemStatus.value = UiStrings.STATUS_DISCONNECTED
    }

    /**
     * Sends a manual override command to the fan hardware.
     */
    fun toggleFanManual(isOn: Boolean) {
        viewModelScope.launch {
            bluetoothManager.sendFanControl(isOn)
            _isFanOn.value = isOn
        }
    }

    /**
     * Adjusts the temperature threshold value and persists the change.
     */
    fun updateThreshold(delta: Int) {
        val newThreshold = _threshold.value + delta
        viewModelScope.launch {
            repository.putInt(Constants.THRESHOLD_KEY, newThreshold)
        }
    }

    /**
     * Cleanup resources when the ViewModel is destroyed.
     */
    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
