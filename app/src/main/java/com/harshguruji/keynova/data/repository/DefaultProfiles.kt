package com.harshguruji.keynova.data.repository

import android.view.KeyEvent
import android.view.MotionEvent
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.ControlType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.Profile
import com.harshguruji.keynova.data.model.VirtualControl

object DefaultProfiles {

    fun createFpsProfile(): Profile {
        val profileId = "profile_fps_gaming"
        val joystickControl = VirtualControl(
            id = "ctrl_joystick_fps",
            profileId = profileId,
            name = "WASD Movement",
            type = ControlType.JOYSTICK,
            posX = 0.18f,
            posY = 0.70f,
            width = 120f,
            height = 120f,
            radius = 60f,
            assignedKeyLabel = "W A S D",
            styleColorHex = "#00F2FE"
        )

        val fireControl = VirtualControl(
            id = "ctrl_fire_fps",
            profileId = profileId,
            name = "Fire",
            type = ControlType.FIRE_BUTTON,
            posX = 0.85f,
            posY = 0.65f,
            width = 72f,
            height = 72f,
            assignedKeyLabel = "M-Left",
            styleColorHex = "#EF4444"
        )

        val aimControl = VirtualControl(
            id = "ctrl_aim_fps",
            profileId = profileId,
            name = "Aim / ADS",
            type = ControlType.AIM_BUTTON,
            posX = 0.75f,
            posY = 0.50f,
            width = 64f,
            height = 64f,
            assignedKeyLabel = "M-Right",
            styleColorHex = "#10B981"
        )

        val jumpControl = VirtualControl(
            id = "ctrl_jump_fps",
            profileId = profileId,
            name = "Jump",
            type = ControlType.BUTTON,
            posX = 0.90f,
            posY = 0.45f,
            width = 56f,
            height = 56f,
            assignedKeyLabel = "SPACE",
            styleColorHex = "#3B82F6"
        )

        val reloadControl = VirtualControl(
            id = "ctrl_reload_fps",
            profileId = profileId,
            name = "Reload",
            type = ControlType.BUTTON,
            posX = 0.82f,
            posY = 0.35f,
            width = 52f,
            height = 52f,
            assignedKeyLabel = "R",
            styleColorHex = "#F59E0B"
        )

        val crouchControl = VirtualControl(
            id = "ctrl_crouch_fps",
            profileId = profileId,
            name = "Crouch",
            type = ControlType.BUTTON,
            posX = 0.70f,
            posY = 0.80f,
            width = 54f,
            height = 54f,
            assignedKeyLabel = "C",
            styleColorHex = "#8B5CF6"
        )

        val mappings = listOf(
            Mapping(
                id = "map_move_up",
                profileId = profileId,
                name = "Move Forward",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_W,
                actionType = ActionType.JOYSTICK_MOVE,
                actionMode = ActionMode.HOLD,
                targetControlId = joystickControl.id
            ),
            Mapping(
                id = "map_move_left",
                profileId = profileId,
                name = "Move Left",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_A,
                actionType = ActionType.JOYSTICK_MOVE,
                actionMode = ActionMode.HOLD,
                targetControlId = joystickControl.id
            ),
            Mapping(
                id = "map_move_down",
                profileId = profileId,
                name = "Move Backward",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_S,
                actionType = ActionType.JOYSTICK_MOVE,
                actionMode = ActionMode.HOLD,
                targetControlId = joystickControl.id
            ),
            Mapping(
                id = "map_move_right",
                profileId = profileId,
                name = "Move Right",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_D,
                actionType = ActionType.JOYSTICK_MOVE,
                actionMode = ActionMode.HOLD,
                targetControlId = joystickControl.id
            ),
            Mapping(
                id = "map_mouse_fire",
                profileId = profileId,
                name = "Fire Trigger",
                deviceType = DeviceType.MOUSE,
                inputType = InputType.MOUSE_BUTTON,
                inputCode = MotionEvent.BUTTON_PRIMARY,
                actionType = ActionType.FIRE_ACTION,
                actionMode = ActionMode.HOLD,
                targetControlId = fireControl.id
            ),
            Mapping(
                id = "map_mouse_aim",
                profileId = profileId,
                name = "Aim Sight",
                deviceType = DeviceType.MOUSE,
                inputType = InputType.MOUSE_BUTTON,
                inputCode = MotionEvent.BUTTON_SECONDARY,
                actionType = ActionType.AIM_ACTION,
                actionMode = ActionMode.TOGGLE,
                targetControlId = aimControl.id
            ),
            Mapping(
                id = "map_key_jump",
                profileId = profileId,
                name = "Jump Action",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_SPACE,
                actionType = ActionType.TAP_ACTION,
                actionMode = ActionMode.TAP,
                targetControlId = jumpControl.id
            ),
            Mapping(
                id = "map_key_reload",
                profileId = profileId,
                name = "Reload Magazine",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_R,
                actionType = ActionType.TAP_ACTION,
                actionMode = ActionMode.TAP,
                targetControlId = reloadControl.id
            ),
            Mapping(
                id = "map_key_crouch",
                profileId = profileId,
                name = "Crouch Stance",
                deviceType = DeviceType.KEYBOARD,
                inputType = InputType.KEYBOARD_KEY,
                inputCode = KeyEvent.KEYCODE_C,
                actionType = ActionType.TOGGLE_ACTION,
                actionMode = ActionMode.TOGGLE,
                targetControlId = crouchControl.id
            )
        )

        return Profile(
            id = profileId,
            name = "FPS Gaming Pro",
            description = "Optimized for Battle Royale & First-Person Shooters (WASD, Mouse Aim & Fire)",
            isDefault = true,
            cursorSensitivityX = 1.2f,
            cursorSensitivityY = 1.2f,
            cursorAcceleration = 1.0f,
            mappings = mappings,
            controls = listOf(joystickControl, fireControl, aimControl, jumpControl, reloadControl, crouchControl)
        )
    }

    fun createMobaProfile(): Profile {
        val profileId = "profile_moba_arena"
        val qSkill = VirtualControl(
            id = "ctrl_moba_q", profileId = profileId, name = "Skill 1 (Q)",
            type = ControlType.BUTTON, posX = 0.70f, posY = 0.75f, assignedKeyLabel = "Q", styleColorHex = "#00F2FE"
        )
        val wSkill = VirtualControl(
            id = "ctrl_moba_w", profileId = profileId, name = "Skill 2 (W)",
            type = ControlType.BUTTON, posX = 0.78f, posY = 0.65f, assignedKeyLabel = "W", styleColorHex = "#3B82F6"
        )
        val eSkill = VirtualControl(
            id = "ctrl_moba_e", profileId = profileId, name = "Skill 3 (E)",
            type = ControlType.BUTTON, posX = 0.86f, posY = 0.55f, assignedKeyLabel = "E", styleColorHex = "#8B5CF6"
        )
        val rUlt = VirtualControl(
            id = "ctrl_moba_r", profileId = profileId, name = "Ultimate (R)",
            type = ControlType.BUTTON, posX = 0.90f, posY = 0.38f, assignedKeyLabel = "R", styleColorHex = "#EC4899"
        )

        val mappings = listOf(
            Mapping("map_moba_q", profileId, "Skill Q", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_Q, emptyList(), ActionType.TAP_ACTION, targetControlId = qSkill.id),
            Mapping("map_moba_w", profileId, "Skill W", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_W, emptyList(), ActionType.TAP_ACTION, targetControlId = wSkill.id),
            Mapping("map_moba_e", profileId, "Skill E", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_E, emptyList(), ActionType.TAP_ACTION, targetControlId = eSkill.id),
            Mapping("map_moba_r", profileId, "Ultimate R", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_R, emptyList(), ActionType.TAP_ACTION, targetControlId = rUlt.id)
        )

        return Profile(
            id = profileId,
            name = "MOBA / Battle Arena",
            description = "Skill casting setup for arena titles with QWER layout",
            isDefault = false,
            mappings = mappings,
            controls = listOf(qSkill, wSkill, eSkill, rUlt)
        )
    }

    fun createRetroGamepadProfile(): Profile {
        val profileId = "profile_retro_gamepad"
        val dpad = VirtualControl(
            id = "ctrl_dpad", profileId = profileId, name = "D-Pad",
            type = ControlType.DPAD, posX = 0.20f, posY = 0.65f, width = 110f, height = 110f, assignedKeyLabel = "Arrows", styleColorHex = "#10B981"
        )
        val btnA = VirtualControl(
            id = "ctrl_btn_a", profileId = profileId, name = "Action A",
            type = ControlType.BUTTON, posX = 0.88f, posY = 0.70f, assignedKeyLabel = "K", styleColorHex = "#10B981"
        )
        val btnB = VirtualControl(
            id = "ctrl_btn_b", profileId = profileId, name = "Action B",
            type = ControlType.BUTTON, posX = 0.80f, posY = 0.78f, assignedKeyLabel = "J", styleColorHex = "#EF4444"
        )

        val mappings = listOf(
            Mapping("map_dpad_up", profileId, "Up", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_DPAD_UP, emptyList(), ActionType.HOLD_ACTION, targetControlId = dpad.id),
            Mapping("map_dpad_down", profileId, "Down", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_DPAD_DOWN, emptyList(), ActionType.HOLD_ACTION, targetControlId = dpad.id),
            Mapping("map_dpad_left", profileId, "Left", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_DPAD_LEFT, emptyList(), ActionType.HOLD_ACTION, targetControlId = dpad.id),
            Mapping("map_dpad_right", profileId, "Right", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_DPAD_RIGHT, emptyList(), ActionType.HOLD_ACTION, targetControlId = dpad.id),
            Mapping("map_btn_a", profileId, "Button A", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_K, emptyList(), ActionType.TAP_ACTION, targetControlId = btnA.id),
            Mapping("map_btn_b", profileId, "Button B", DeviceType.KEYBOARD, InputType.KEYBOARD_KEY, KeyEvent.KEYCODE_J, emptyList(), ActionType.TAP_ACTION, targetControlId = btnB.id)
        )

        return Profile(
            id = profileId,
            name = "Retro Emulator Gamepad",
            description = "Classic D-Pad and dual action buttons for emulators and arcade games",
            isDefault = false,
            mappings = mappings,
            controls = listOf(dpad, btnA, btnB)
        )
    }
}
