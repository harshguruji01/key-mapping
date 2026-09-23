package com.harshguruji.keynova.data.model

data class KeyCombination(
    val primaryCode: Int,
    val modifierFlags: Int = 0 // META_SHIFT_ON, META_CTRL_ON, META_ALT_ON, etc.
) {
    fun toDisplayText(): String {
        val parts = mutableListOf<String>()
        if (modifierFlags and android.view.KeyEvent.META_CTRL_ON != 0) parts.add("CTRL")
        if (modifierFlags and android.view.KeyEvent.META_ALT_ON != 0) parts.add("ALT")
        if (modifierFlags and android.view.KeyEvent.META_SHIFT_ON != 0) parts.add("SHIFT")
        if (modifierFlags and android.view.KeyEvent.META_META_ON != 0) parts.add("META")
        parts.add(android.view.KeyEvent.keyCodeToString(primaryCode).removePrefix("KEYCODE_"))
        return parts.joinToString(" + ")
    }
}

data class Mapping(
    val id: String,
    val profileId: String,
    val name: String,
    val deviceType: DeviceType,
    val inputType: InputType,
    val inputCode: Int, // e.g. KeyEvent.KEYCODE_W or MotionEvent.BUTTON_PRIMARY
    val secondaryCodes: List<Int> = emptyList(), // e.g. [KEYCODE_CTRL_LEFT]
    val actionType: ActionType,
    val actionMode: ActionMode = ActionMode.TAP,
    val targetControlId: String? = null,
    val enabled: Boolean = true,
    val sensitivity: Float = 1.0f,
    val holdThresholdMs: Long = 300L,
    val repeatIntervalMs: Long = 100L,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class VirtualControl(
    val id: String,
    val profileId: String,
    val name: String,
    val type: ControlType,
    val posX: Float, // percentage 0.0f - 1.0f across canvas width
    val posY: Float, // percentage 0.0f - 1.0f across canvas height
    val width: Float = 64f, // dp or percentage
    val height: Float = 64f,
    val opacity: Float = 0.85f,
    val radius: Float = 40f, // for joystick
    val deadZone: Float = 0.15f,
    val assignedKeyLabel: String = "",
    val styleColorHex: String = "#00F2FE",
    val isVisible: Boolean = true
)

data class Profile(
    val id: String,
    val name: String,
    val description: String,
    val targetPackage: String? = null,
    val isDefault: Boolean = false,
    val cursorSensitivityX: Float = 1.0f,
    val cursorSensitivityY: Float = 1.0f,
    val cursorAcceleration: Float = 1.0f,
    val scrollSensitivity: Float = 1.0f,
    val mappings: List<Mapping> = emptyList(),
    val controls: List<VirtualControl> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class DeviceInfo(
    val id: Int,
    val name: String,
    val vendorId: Int,
    val productId: Int,
    val descriptor: String,
    val type: DeviceType,
    val isConnected: Boolean = true,
    val sources: Int = 0,
    val keyboardType: Int = 0,
    val hasVibrator: Boolean = false
)
