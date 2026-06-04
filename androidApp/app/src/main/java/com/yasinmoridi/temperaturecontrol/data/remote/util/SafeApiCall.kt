package com.yasinmoridi.temperaturecontrol.data.remote.util

import com.yasinmoridi.temperaturecontrol.data.remote.model.ApiResponse
import com.yasinmoridi.temperaturecontrol.data.remote.model.NetworkMessages
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/**
 * A wrapper function to execute network requests safely and handle exceptions.
 * It transforms the Retrofit Response into a sealed [NetworkResult] type.
 *
 * @param T The type of data expected in the response.
 * @param apiCall A suspend function that executes the network request.
 * @return A [NetworkResult] containing the data or an error message.
 */
suspend fun <T> safeApiCall(
    apiCall: suspend () -> Response<ApiResponse<T>>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        val code = response.code()
        
        if (response.isSuccessful) {
            val body = response.body()
            when {
                // Handle case where body is missing
                body == null -> {
                    NetworkResult.Error(
                        message = NetworkMessages.EMPTY_BODY,
                        code = code
                    )
                }

                // Handle successful business logic from the server
                body.success && body.data != null -> {
                    NetworkResult.Success(
                        data = body.data,
                        message = body.message ?: NetworkMessages.SUCCESS,
                        code = code
                    )
                }

                // Handle server-side failures returned with a 200 OK
                else -> {
                    NetworkResult.Error(
                        message = body.message ?: NetworkMessages.REQUEST_FAILED,
                        code = code
                    )
                }
            }
        } else {
            // Handle non-2xx HTTP responses
            NetworkResult.Error(
                message = NetworkMessages.httpError(code),
                code = code
            )
        }
    } catch (e: IOException) {
        // Handle connectivity issues (e.g., no internet, timeout)
        NetworkResult.Error(
            message = NetworkMessages.NETWORK_ERROR,
            throwable = e
        )
    } catch (e: HttpException) {
        // Handle specific Retrofit HTTP exceptions
        NetworkResult.Error(
            message = NetworkMessages.httpError(e.code()),
            code = e.code(),
            throwable = e
        )
    } catch (e: Exception) {
        // Handle any other unexpected exceptions
        NetworkResult.Error(
            message = NetworkMessages.UNKNOWN_ERROR,
            throwable = e
        )
    }
}
