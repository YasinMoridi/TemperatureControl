package com.yasinmoridi.temperaturecontrol.data.remote.model

data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)