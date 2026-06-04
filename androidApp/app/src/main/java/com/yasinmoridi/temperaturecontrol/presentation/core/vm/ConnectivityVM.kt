package com.yasinmoridi.temperaturecontrol.presentation.core.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yasinmoridi.temperaturecontrol.utils.network.ConnectivityObserver
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ConnectivityVM(
    connectivityObserver: ConnectivityObserver
) : ViewModel() {

    val isOnline = connectivityObserver.online
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
}