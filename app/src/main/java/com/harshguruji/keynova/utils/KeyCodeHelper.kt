package com.harshguruji.keynova.utils

import android.view.KeyEvent
import android.view.MotionEvent

object KeyCodeHelper {

    fun getKeyLabel(keyCode: Int): String {
        return when (keyCode) {
            KeyEvent.KEYCODE_SPACE -> "SPACE"
            KeyEvent.KEYCODE_ENTER -> "ENTER"
            KeyEvent.KEYCODE_DEL -> "BACKSPACE"
            KeyEvent.KEYCODE_TAB -> "TAB"
            KeyEvent.KEYCODE_ESCAPE -> "ESC"
            KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> "SHIFT"
            KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_CTRL_RIGHT -> "CTRL"
            KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> "ALT"
            KeyEvent.KEYCODE_DPAD_UP -> "UP"
            KeyEvent.KEYCODE_DPAD_DOWN -> "DOWN"
            KeyEvent.KEYCODE_DPAD_LEFT -> "LEFT"
            KeyEvent.KEYCODE_DPAD_RIGHT -> "RIGHT"
            else -> {
                val str = KeyEvent.keyCodeToString(keyCode)
                str.removePrefix("KEYCODE_")
            }
        }
    }

    fun getMouseButtonLabel(buttonCode: Int): String {
        return when (buttonCode) {
            MotionEvent.BUTTON_PRIMARY -> "Mouse Left"
            MotionEvent.BUTTON_SECONDARY -> "Mouse Right"
            MotionEvent.BUTTON_TERTIARY -> "Mouse Middle"
            MotionEvent.BUTTON_BACK -> "Mouse Back"
            MotionEvent.BUTTON_FORWARD -> "Mouse Forward"
            else -> "Mouse Button $buttonCode"
        }
    }
}
