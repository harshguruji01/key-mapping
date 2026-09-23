package com.harshguruji.keynova.data.model

enum class DeviceType(val displayName: String) {
    KEYBOARD("Keyboard"),
    MOUSE("Mouse"),
    GAMEPAD("Gamepad / Controller"),
    JOYSTICK("Joystick"),
    STYLUS("Stylus"),
    OTHER("Other Device")
}

enum class InputType(val displayName: String) {
    KEYBOARD_KEY("Keyboard Key"),
    KEY_COMBINATION("Key Combination"),
    MOUSE_BUTTON("Mouse Button"),
    MOUSE_MOTION("Mouse Motion"),
    CONTROLLER_BUTTON("Controller Button"),
    CONTROLLER_AXIS("Controller Axis")
}

enum class ActionType(val displayName: String) {
    TAP_ACTION("Tap"),
    HOLD_ACTION("Hold"),
    TOGGLE_ACTION("Toggle"),
    REPEAT_ACTION("Repeat"),
    JOYSTICK_MOVE("Joystick Move"),
    CURSOR_MOVE("Virtual Cursor"),
    FIRE_ACTION("Fire / Primary Action"),
    AIM_ACTION("Aim / Secondary Action"),
    CUSTOM_ACTION("Custom Action")
}

enum class ActionMode(val displayName: String) {
    TAP("Tap"),
    HOLD("Hold"),
    TOGGLE("Toggle"),
    REPEAT("Repeat")
}

enum class ControlType(val displayName: String) {
    BUTTON("Virtual Button"),
    JOYSTICK("Virtual Joystick"),
    CURSOR("Virtual Cursor / Aim"),
    DPAD("Directional Pad"),
    FIRE_BUTTON("Fire Button"),
    AIM_BUTTON("Aim Button"),
    TOUCH_AREA("Touch Area")
}

enum class MappingState {
    IDLE,
    PRESSED,
    HELD,
    TOGGLED_ON,
    TOGGLED_OFF,
    RELEASED
}
