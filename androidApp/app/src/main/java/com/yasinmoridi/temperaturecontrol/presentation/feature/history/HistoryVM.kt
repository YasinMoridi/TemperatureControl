package com.yasinmoridi.temperaturecontrol.presentation.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.data.bluetooth.BluetoothManager
import com.yasinmoridi.temperaturecontrol.presentation.core.UiStrings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class representing a single temperature log entry.
 */
data class LogEntry(
    val time: String,
    val temperature: String,
    val timestamp: Long = System.currentTimeMillis()
)

class HistoryVM(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    
    // Currently selected time range for filtering logs (e.g., 24H, 7D)
    private val _selectedRange = MutableStateFlow(UiStrings.RANGE_24H)
    val selectedRange = _selectedRange.asStateFlow()

    // Internal list to store all received logs from the device
    private val allLogs = mutableListOf<LogEntry>()
    
    // Filtered list of logs based on the selected time range
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs = _logs.asStateFlow()

    // Observable for the maximum temperature within the filtered logs
    val maxTemp = _logs.map { list ->
        list.maxOfOrNull { it.temperature.replace(UiStrings.UNIT_CELSIUS, "").trim().toFloatOrNull() ?: 0f } ?: 0f
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    // Observable count of temperature alerts (readings above 30°C)
    val alertsCount = _logs.map { list ->
        list.count { (it.temperature.replace(UiStrings.UNIT_CELSIUS, "").trim().toFloatOrNull() ?: 0f) > 30f }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Formatter for displaying log times in HH:mm format
    private val timeFormatter = SimpleDateFormat(UiStrings.TIME_FORMAT_HM, Locale.getDefault())

    init {
        observeBluetoothData()
    }

    /**
     * Listens to real-time sensor data from the Bluetooth manager and records it.
     */
    private fun observeBluetoothData() {
        viewModelScope.launch {
            bluetoothManager.sensorData.collect { result ->
                result.onSuccess { data ->
                    val newEntry = LogEntry(
                        time = timeFormatter.format(Date()),
                        temperature = UiStrings.TEMP_FORMAT.format(data.temperature)
                    )
                    // Add new entry to the top of the list
                    allLogs.add(0, newEntry)
                    
                    // Keep the memory footprint manageable (limit to 1000 records)
                    if (allLogs.size > 1000) allLogs.removeAt(allLogs.size - 1)
                    
                    updateFilteredLogs()
                }
            }
        }
    }

    /**
     * Updates the active time range filter.
     */
    fun selectRange(range: String) {
        _selectedRange.value = range
        updateFilteredLogs()
    }

    /**
     * Filters the total log history based on the selected time window (1H, 24H, 7D, 30D).
     */
    private fun updateFilteredLogs() {
        val now = System.currentTimeMillis()
        val durationMs = when (_selectedRange.value) {
            UiStrings.RANGE_1H -> 60 * 60 * 1000L
            UiStrings.RANGE_24H -> 24 * 60 * 60 * 1000L
            UiStrings.RANGE_7D -> 7 * 24 * 60 * 60 * 1000L
            UiStrings.RANGE_30D -> 30 * 24 * 60 * 60 * 1000L
            else -> 24 * 60 * 60 * 1000L
        }

        _logs.value = allLogs.filter { now - it.timestamp <= durationMs }
    }
}
