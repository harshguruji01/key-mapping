package com.harshguruji.keynova.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.harshguruji.keynova.KeyNovaApp

class KeyNovaAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        KeyNovaApp.instance.isAccessibilityEnabled.value = true
        KeyNovaApp.instance.mappingEngine.diagnostics.logDiagnosticMessage("Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // We do NOT collect arbitrary screen content or passwords
    }

    override fun onInterrupt() {
        KeyNovaApp.instance.isAccessibilityEnabled.value = false
        KeyNovaApp.instance.mappingEngine.diagnostics.logDiagnosticMessage("Accessibility Service Interrupted")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        // Forward physical hardware key event to mapping engine
        val consumed = KeyNovaApp.instance.mappingEngine.processKeyEvent(event)
        return consumed
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyNovaApp.instance.isAccessibilityEnabled.value = false
    }

    /**
     * Dispatches a simulated tap gesture at the specified screen coordinates when requested by a virtual mapping.
     */
    fun dispatchTapGesture(x: Float, y: Float, durationMs: Long = 50L) {
        val path = Path().apply {
            moveTo(x, y)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, durationMs)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()
        dispatchGesture(gesture, null, null)
    }
}
