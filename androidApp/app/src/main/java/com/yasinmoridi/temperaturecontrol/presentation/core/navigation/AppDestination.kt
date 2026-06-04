package com.yasinmoridi.temperaturecontrol.presentation.core.navigation

import kotlinx.serialization.Serializable

sealed interface AppDestination {

    @Serializable
    data object Splash : AppDestination

    @Serializable
    data object DashboardRoot : AppDestination

    @Serializable
    data object Dash : AppDestination

    @Serializable
    data object Devices : AppDestination

    @Serializable
    data object History : AppDestination

    @Serializable
    data object Settings : AppDestination
}