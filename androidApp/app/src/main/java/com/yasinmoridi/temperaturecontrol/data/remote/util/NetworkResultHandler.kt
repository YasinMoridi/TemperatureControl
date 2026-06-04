package com.yasinmoridi.temperaturecontrol.data.remote.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * A side effect Composable that reacts to changes in [NetworkResult] state.
 * Useful for triggering one-time events like showing Snackbars or Navigating.
 */
@Composable
fun <T> CheckResponse(
    state: NetworkResult<T>,
    onSuccess: suspend (NetworkResult.Success<T>) -> Unit,
    onError: suspend (String) -> Unit = {},
    onLoading: () -> Unit = {},
) {
    LaunchedEffect(state) {
        when (state) {
            is NetworkResult.Loading -> onLoading()
            is NetworkResult.Success -> onSuccess(state)
            is NetworkResult.Error -> onError(state.message)
            is NetworkResult.Idle -> {}
        }
    }
}

/**
 * A UI wrapper that automatically handles different states of a [NetworkResult].
 * It displays loading, error, or success content with a smooth transition.
 *
 * @param result The current state of the network request.
 * @param onRetry Callback triggered when the retry button is clicked in the error state.
 * @param content The Composable to display when the state is Success.
 */
@Composable
fun <T> NetworkResultHandler(
    result: NetworkResult<T>,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    loadingContent: @Composable () -> Unit = {
        DefaultLoading()
    },
    errorContent: @Composable (message: String, onRetry: () -> Unit) -> Unit = { message, retry ->
        ErrorContent(
            message = message,
            onRetry = retry
        )
    },
    content: @Composable (data: T) -> Unit
) {

    AnimatedContent(
        modifier = modifier,
        targetState = result,
        label = "NetworkResultAnimation",
        transitionSpec = {
            fadeIn(
                animationSpec = tween(300)
            ) togetherWith fadeOut(
                animationSpec = tween(300)
            )
        }
    ) { state ->
        when (state) {
            is NetworkResult.Loading -> {
                loadingContent()
            }

            is NetworkResult.Error -> {
                errorContent(
                    state.message,
                    onRetry
                )
            }

            is NetworkResult.Success -> {
                content(state.data)
            }

            is NetworkResult.Idle -> {}
        }
    }
}

/**
 * Default loading indicator used by [NetworkResultHandler].
 */
@Composable
fun DefaultLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Default error view used by [NetworkResultHandler] with a retry button.
 */
@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("تلاش مجدد") // Retry button text in Persian
        }
    }
}
