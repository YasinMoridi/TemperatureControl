package com.yasinmoridi.temperaturecontrol.presentation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.data.dataStore.DataStoreRepository
import com.yasinmoridi.temperaturecontrol.utils.Constants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen, handling persistent configuration and app state.
 */
class SettingsVM(
    private val repository: DataStoreRepository
) : ViewModel() {
    
    // State for the critical temperature threshold
    private val _criticalThreshold = MutableStateFlow(30f)
    val criticalThreshold = _criticalThreshold.asStateFlow()

    // State for push notification preference
    private val _pushNotificationsEnabled = MutableStateFlow(true)
    val pushNotificationsEnabled = _pushNotificationsEnabled.asStateFlow()

    // State for dark mode preference
    private val _darkModeEnabled = MutableStateFlow(true)
    val darkModeEnabled = _darkModeEnabled.asStateFlow()

    // State for power saving mode preference
    private val _powerSavingEnabled = MutableStateFlow(false)
    val powerSavingEnabled = _powerSavingEnabled.asStateFlow()

    init {
        // Observe persistent threshold value from DataStore on initialization
        viewModelScope.launch {
            repository.observeInt(Constants.THRESHOLD_KEY).collectLatest { value ->
                _criticalThreshold.value = (value ?: 30).toFloat()
            }
        }
    }

    /**
     * Updates the temperature threshold and persists it to DataStore.
     */
    fun updateThreshold(value: Float) {
        _criticalThreshold.value = value
        viewModelScope.launch {
            repository.putInt(Constants.THRESHOLD_KEY, value.toInt())
        }
    }

    /**
     * Toggles push notifications status.
     */
    fun togglePushNotifications(enabled: Boolean) {
        _pushNotificationsEnabled.value = enabled
    }

    /**
     * Toggles dark mode appearance.
     */
    fun toggleDarkMode(enabled: Boolean) {
        _darkModeEnabled.value = enabled
    }

    /**
     * Toggles power saving optimizations.
     */
    fun togglePowerSaving(enabled: Boolean) {
        _powerSavingEnabled.value = enabled
    }
}
