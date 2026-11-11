package com.ancientkeys.keyboard.ime.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ancientkeys.keyboard.ui.theme.AncientKeysDesignTokens
import com.ancientkeys.keyboard.ui.theme.ancientKeysColors
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Handles repeat timing calculations and UI components for continuous key actions (such as Backspace).
 */
object RepeatBackspaceHelper {
    const val INITIAL_DELAY_MS = 400L
    const val REPEAT_INTERVAL_MS = 50L
    const val ACCELERATED_INTERVAL_MS = 35L
    const val ACCELERATION_THRESHOLD = 15 // after 15 repeats (~750ms), speed up to 35ms

    /**
     * Computes the delay before the next deletion pulse based on repeat count.
     */
    fun computeDelay(repeatCount: Int): Long {
        return if (repeatCount >= ACCELERATION_THRESHOLD) ACCELERATED_INTERVAL_MS else REPEAT_INTERVAL_MS
    }

    /**
     * Simulates the total number of deletion events for a given hold duration in milliseconds.
     * Useful for deterministic unit testing.
     */
    fun simulateRepeatSequence(durationMs: Long): Int {
        if (durationMs <= 0) return 0
        var events = 1 // First deletion on touch down
        if (durationMs <= INITIAL_DELAY_MS) return events

        var elapsed = INITIAL_DELAY_MS
        var repeatCount = 0
        while (elapsed < durationMs) {
            val step = computeDelay(repeatCount)
            elapsed += step
            if (elapsed <= durationMs) {
                events++
                repeatCount++
            }
        }
        return events
    }
}

/**
 * A key button that triggers an initial click immediately on press, and if held down,
 * repeats the click continuously with standard acceleration.
 */
@Composable
fun RepeatKeyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = AncientKeysDesignTokens.Shapes.actionKeyShape,
    containerColor: Color = MaterialTheme.ancientKeysColors.keyActionBackground,
    contentColor: Color = MaterialTheme.ancientKeysColors.keyActionText,
    border: BorderStroke? = BorderStroke(1.dp, MaterialTheme.ancientKeysColors.keyBorder.copy(alpha = 0.6f)),
    elevation: Dp = AncientKeysDesignTokens.Elevation.keyElevation,
    contentDescription: String = "Backspace",
    onHaptic: () -> Unit = {},
    content: @Composable () -> Unit
) {
    val currentOnClick by rememberUpdatedState(onClick)
    val currentOnHaptic by rememberUpdatedState(onHaptic)
    var isPressed by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .semantics {
                this.role = Role.Button
                this.contentDescription = contentDescription
            }
            .pointerInput(enabled) {
                if (!enabled) return@pointerInput
                coroutineScope {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        down.consume()
                        isPressed = true
                        currentOnHaptic()
                        currentOnClick()

                        val repeatJob: Job = launch {
                            delay(RepeatBackspaceHelper.INITIAL_DELAY_MS)
                            var count = 0
                            while (isActive) {
                                currentOnHaptic()
                                currentOnClick()
                                count++
                                val delayMs = RepeatBackspaceHelper.computeDelay(count)
                                delay(delayMs)
                            }
                        }

                        // Wait for release or cancellation
                        val up = waitForUpOrCancellation()
                        repeatJob.cancel()
                        isPressed = false
                        up?.consume()
                    }
                }
            },
        shape = shape,
        color = if (isPressed) containerColor.copy(alpha = 0.8f) else containerColor,
        contentColor = contentColor,
        border = border,
        shadowElevation = if (isPressed) 0.dp else elevation
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

