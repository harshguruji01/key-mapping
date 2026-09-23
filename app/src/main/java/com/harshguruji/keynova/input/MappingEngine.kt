package com.harshguruji.keynova.input

import android.view.KeyEvent
import android.view.MotionEvent
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MappingEngine(
    val deviceManager: InputDeviceManager,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    val modifierManager = ModifierStateManager()
    val combinationResolver = CombinationResolver()
    val stateManager = MappingStateManager(scope)
    val mouseProcessor = MouseProcessor()
    val mappingResolver = MappingResolver()
    val diagnostics = InputDiagnostics()

    private val _isMasterActive = MutableStateFlow(true)
    val isMasterActive: StateFlow<Boolean> = _isMasterActive.asStateFlow()

    private val _activeProfile = MutableStateFlow<Profile?>(null)
    val activeProfile: StateFlow<Profile?> = _activeProfile.asStateFlow()

    // Temporary capture mode for the Mapper's "Press key / mouse button" interactive UI
    var inputCaptureCallback: ((inputType: com.harshguruji.keynova.data.model.InputType, code: Int, modifiers: List<Int>) -> Unit)? = null

    fun setMasterActive(active: Boolean) {
        _isMasterActive.value = active
        if (!active) {
            stateManager.resetAll()
            modifierManager.reset()
            mouseProcessor.reset()
        }
        diagnostics.logDiagnosticMessage("Master Mapping Switch: ${if (active) "ACTIVE" else "DISABLED"}")
    }

    fun loadProfile(profile: Profile) {
        _activeProfile.value = profile
        mappingResolver.rebuild(profile.mappings)
        mouseProcessor.updateSettings(
            profile.cursorSensitivityX,
            profile.cursorSensitivityY,
            profile.cursorAcceleration,
            profile.scrollSensitivity
        )
        stateManager.resetAll()
        diagnostics.updateMetrics(
            activeMappings = profile.mappings.count { it.enabled },
            connectedDevices = deviceManager.devices.value.size
        )
        diagnostics.logDiagnosticMessage("Profile loaded: '${profile.name}' with ${profile.mappings.size} mappings")
    }

    /**
     * Intercepts and processes hardware key events.
     * Returns true if event was consumed by a configured mapping, false otherwise.
     */
    fun processKeyEvent(event: KeyEvent): Boolean {
        val startNano = System.nanoTime()

        // Check if capture mode is active (for mapping configuration wizard)
        inputCaptureCallback?.let { callback ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                val activeModifiers = mutableListOf<Int>()
                if (event.isCtrlPressed) activeModifiers.add(KeyEvent.KEYCODE_CTRL_LEFT)
                if (event.isAltPressed) activeModifiers.add(KeyEvent.KEYCODE_ALT_LEFT)
                if (event.isShiftPressed) activeModifiers.add(KeyEvent.KEYCODE_SHIFT_LEFT)
                if (event.isMetaPressed) activeModifiers.add(KeyEvent.KEYCODE_META_LEFT)

                callback(com.harshguruji.keynova.data.model.InputType.KEYBOARD_KEY, event.keyCode, activeModifiers)
            }
            return true
        }

        modifierManager.updateFromKeyEvent(event)

        if (!_isMasterActive.value) {
            return false
        }

        val keyCode = event.keyCode
        val action = event.action

        val mapping = combinationResolver.resolve(
            keyCode = keyCode,
            modifierState = modifierManager.modifierState.value,
            comboMappings = mappingResolver.getAllComboMappings(),
            keyMappings = mappingResolver.getAllKeyMappings()
        )

        val consumed = if (mapping != null && mapping.enabled) {
            when (action) {
                KeyEvent.ACTION_DOWN -> {
                    stateManager.onInputDown(mapping)
                    true
                }
                KeyEvent.ACTION_UP -> {
                    stateManager.onInputUp(mapping)
                    true
                }
                else -> false
            }
        } else {
            false
        }

        val duration = System.nanoTime() - startNano
        diagnostics.recordEvent(duration, "Key: ${KeyEvent.keyCodeToString(keyCode).removePrefix("KEYCODE_")}")
        return consumed
    }

    /**
     * Intercepts and processes mouse and motion events.
     * Returns true if event was consumed by a configured mapping, false otherwise.
     */
    fun processGenericMotionEvent(event: MotionEvent): Boolean {
        val startNano = System.nanoTime()

        // Check capture mode for mouse buttons
        inputCaptureCallback?.let { callback ->
            if (event.actionMasked == MotionEvent.ACTION_BUTTON_PRESS || event.actionMasked == MotionEvent.ACTION_DOWN) {
                val button = event.buttonState
                if (button != 0) {
                    callback(com.harshguruji.keynova.data.model.InputType.MOUSE_BUTTON, button, emptyList())
                    return true
                }
            }
        }

        if (!_isMasterActive.value) {
            return false
        }

        val motionData = mouseProcessor.processMotionEvent(event)
        var consumed = false

        if (event.actionMasked == MotionEvent.ACTION_BUTTON_PRESS || event.actionMasked == MotionEvent.ACTION_DOWN) {
            val button = event.buttonState
            val mapping = mappingResolver.getMouseMapping(button)
            if (mapping != null && mapping.enabled) {
                stateManager.onInputDown(mapping)
                consumed = true
            }
        } else if (event.actionMasked == MotionEvent.ACTION_BUTTON_RELEASE || event.actionMasked == MotionEvent.ACTION_UP) {
            val button = event.buttonState
            val mapping = mappingResolver.getMouseMapping(button)
            if (mapping != null && mapping.enabled) {
                stateManager.onInputUp(mapping)
                consumed = true
            }
        }

        if (motionData != null) {
            consumed = true
        }

        val duration = System.nanoTime() - startNano
        diagnostics.recordEvent(duration, "Mouse Motion / Button")
        return consumed
    }
}
