package com.yasinmoridi.temperaturecontrol.presentation.core.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.data.bluetooth.BluetoothManager
import com.yasinmoridi.temperaturecontrol.data.dataStore.DataStoreRepository
import com.yasinmoridi.temperaturecontrol.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainVM(
    private val bluetoothManager: BluetoothManager,
    private val repository: DataStoreRepository
) : ViewModel() {

    private val _isCritical = MutableStateFlow(false)
    val isCritical = _isCritical.asStateFlow()

    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    init {
        observeCriticalStatus()
    }

    private fun observeCriticalStatus() {
        viewModelScope.launch {
            combine(
                bluetoothManager.sensorData,
                repository.observeInt(Constants.THRESHOLD_KEY)
            ) { sensorResult, threshold ->
                val temp = sensorResult.getOrNull()?.temperature ?: 0f
                val currentThreshold = threshold ?: 25
                temp >= currentThreshold
            }.collect { critical ->
                _isCritical.value = critical
            }
        }

        viewModelScope.launch {
            // This is a bit simplified, ideally BluetoothManager should expose a connection state flow
            bluetoothManager.sensorData.collect { result ->
                _isConnected.value = result.isSuccess
            }
        }
    }
}
