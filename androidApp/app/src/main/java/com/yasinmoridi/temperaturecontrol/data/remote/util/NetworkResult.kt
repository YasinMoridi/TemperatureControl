package com.yasinmoridi.temperaturecontrol.data.remote.util

import androidx.compose.runtime.Immutable

/**
 * A sealed interface representing the different states of a network request.
 * It is marked as [Immutable] to optimize Compose recompositions.
 */
@Immutable
sealed interface NetworkResult<out T> {

    /**
     * Represents a successful response from the server.
     * @param data The parsed response body.
     * @param message Success message from the API.
     * @param code The HTTP status code.
     */
    @Immutable
    data class Success<out T>(
        val data: T,
        val message: String,
        val code: Int = 200
    ) : NetworkResult<T>

    /**
     * Represents an error state (network, server, or logic error).
     * @param message A user-readable error message.
     * @param code The HTTP status code if applicable.
     * @param throwable The underlying exception.
     */
    @Immutable
    data class Error(
        val message: String,
        val code: Int? = null,
        val throwable: Throwable? = null
    ) : NetworkResult<Nothing>

    /**
     * State indicating that a network request is currently active.
     */
    data object Loading : NetworkResult<Nothing>

    /**
     * The initial state before any network request has been triggered.
     */
    data object Idle : NetworkResult<Nothing>
}
