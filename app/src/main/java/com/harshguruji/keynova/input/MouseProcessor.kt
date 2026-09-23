package com.harshguruji.keynova.input

import android.os.Build
import android.view.MotionEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.pow
import kotlin.math.sign

data class MouseMotionEventData(
    val deltaX: Float,
    val deltaY: Float,
    val scrollV: Float = 0f,
    val scrollH: Float = 0f,
    val buttonState: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

class MouseProcessor {

    private var sensitivityX = 1.0f
    private var sensitivityY = 1.0f
    private var acceleration = 1.0f
    private var scrollSensitivity = 1.0f

    private var lastRawX = Float.NaN
    private var lastRawY = Float.NaN

    private val _mouseEvents = MutableSharedFlow<MouseMotionEventData>(extraBufferCapacity = 50)
    val mouseEvents: SharedFlow<MouseMotionEventData> = _mouseEvents.asSharedFlow()

    private val _activeButtons = MutableStateFlow(0)
    val activeButtons: StateFlow<Int> = _activeButtons.asStateFlow()

    fun updateSettings(sensX: Float, sensY: Float, accel: Float, scrollSens: Float) {
        this.sensitivityX = sensX
        this.sensitivityY = sensY
        this.acceleration = accel
        this.scrollSensitivity = scrollSens
    }

    fun processMotionEvent(event: MotionEvent): MouseMotionEventData? {
        val action = event.actionMasked

        var dx = 0f
        var dy = 0f
        var vScroll = 0f
        var hScroll = 0f

        // Try getting relative axes if exposed by hardware
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val relX = event.getAxisValue(MotionEvent.AXIS_RELATIVE_X)
            val relY = event.getAxisValue(MotionEvent.AXIS_RELATIVE_Y)
            if (relX != 0f || relY != 0f) {
                dx = relX
                dy = relY
            }
        }

        // Fallback to raw delta if relative axis wasn't available
        if (dx == 0f && dy == 0f) {
            if (!lastRawX.isNaN() && !lastRawY.isNaN()) {
                dx = event.rawX - lastRawX
                dy = event.rawY - lastRawY
            }
        }
        lastRawX = event.rawX
        lastRawY = event.rawY

        if (action == MotionEvent.ACTION_SCROLL) {
            vScroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL) * scrollSensitivity
            hScroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL) * scrollSensitivity
        }

        val buttonState = event.buttonState
        _activeButtons.value = buttonState

        if (dx != 0f || dy != 0f || vScroll != 0f || hScroll != 0f) {
            // Apply sensitivity & non-linear acceleration curve
            val finalDx = applyAcceleration(dx * sensitivityX)
            val finalDy = applyAcceleration(dy * sensitivityY)

            val data = MouseMotionEventData(
                deltaX = finalDx,
                deltaY = finalDy,
                scrollV = vScroll,
                scrollH = hScroll,
                buttonState = buttonState
            )
            _mouseEvents.tryEmit(data)
            return data
        }

        return null
    }

    private fun applyAcceleration(delta: Float): Float {
        if (acceleration <= 1.0f || delta == 0f) return delta
        val sign = delta.sign
        val mag = kotlin.math.abs(delta)
        // Gentle exponential scaling for high speed flick movements
        val scaled = mag * (1f + (acceleration - 1f) * (mag / 50f).coerceAtMost(2.5f))
        return sign * scaled
    }

    fun reset() {
        lastRawX = Float.NaN
        lastRawY = Float.NaN
        _activeButtons.value = 0
    }
}
