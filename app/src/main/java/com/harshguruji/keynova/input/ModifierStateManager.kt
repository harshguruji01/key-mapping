package com.harshguruji.keynova.input

import android.view.KeyEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ModifierState(
    val isShiftPressed: Boolean = false,
    val isCtrlPressed: Boolean = false,
    val isAltPressed: Boolean = false,
    val isMetaPressed: Boolean = false,
    val isCapsLockOn: Boolean = false,
    val isNumLockOn: Boolean = false
) {
    fun toMetaStateFlags(): Int {
        var flags = 0
        if (isShiftPressed) flags = flags or KeyEvent.META_SHIFT_ON
        if (isCtrlPressed) flags = flags or KeyEvent.META_CTRL_ON
        if (isAltPressed) flags = flags or KeyEvent.META_ALT_ON
        if (isMetaPressed) flags = flags or KeyEvent.META_META_ON
        if (isCapsLockOn) flags = flags or KeyEvent.META_CAPS_LOCK_ON
        if (isNumLockOn) flags = flags or KeyEvent.META_NUM_LOCK_ON
        return flags
    }

    fun hasAnyActiveModifier(): Boolean {
        return isShiftPressed || isCtrlPressed || isAltPressed || isMetaPressed
    }
}

class ModifierStateManager {

    private val _modifierState = MutableStateFlow(ModifierState())
    val modifierState: StateFlow<ModifierState> = _modifierState.asStateFlow()

    fun updateFromKeyEvent(event: KeyEvent) {
        val keyCode = event.keyCode
        val isDown = event.action == KeyEvent.ACTION_DOWN
        val current = _modifierState.value

        var newShift = current.isShiftPressed
        var newCtrl = current.isCtrlPressed
        var newAlt = current.isAltPressed
        var newMeta = current.isMetaPressed

        when (keyCode) {
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> newShift = isDown
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_CTRL_RIGHT -> newCtrl = isDown
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> newAlt = isDown
            KeyEvent.KEYCODE_META_LEFT, KeyEvent.KEYCODE_META_RIGHT -> newMeta = isDown
        }

        // Check hardware lock flags if available
        val capsLock = event.isCapsLockOn
        val numLock = event.isNumLockOn

        _modifierState.value = ModifierState(
            isShiftPressed = newShift || event.isShiftPressed,
            isCtrlPressed = newCtrl || event.isCtrlPressed,
            isAltPressed = newAlt || event.isAltPressed,
            isMetaPressed = newMeta || event.isMetaPressed,
            isCapsLockOn = capsLock,
            isNumLockOn = numLock
        )
    }

    fun reset() {
        _modifierState.value = ModifierState()
    }
}
