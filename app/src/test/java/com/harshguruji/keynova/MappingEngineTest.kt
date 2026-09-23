package com.harshguruji.keynova

import android.view.KeyEvent
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.KeyCombination
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.input.CombinationResolver
import com.harshguruji.keynova.input.MappingResolver
import com.harshguruji.keynova.input.ModifierState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MappingEngineTest {

    private lateinit var resolver: MappingResolver
    private lateinit var comboResolver: CombinationResolver

    @Before
    fun setup() {
        resolver = MappingResolver()
        comboResolver = CombinationResolver()
    }

    @Test
    fun testFastKeyLookup() {
        val singleKeyMapping = Mapping(
            id = "map_w",
            profileId = "prof_test",
            name = "Move Forward",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_W,
            actionType = ActionType.TAP_ACTION
        )

        resolver.rebuild(listOf(singleKeyMapping))

        val result = resolver.getKeyMapping(KeyEvent.KEYCODE_W)
        assertNotNull(result)
        assertEquals("Move Forward", result?.name)

        val unmapped = resolver.getKeyMapping(KeyEvent.KEYCODE_Z)
        assertNull(unmapped)
    }

    @Test
    fun testExactCombinationPriorityOverSingleKey() {
        val singleKeyMapping = Mapping(
            id = "map_a",
            profileId = "prof_test",
            name = "Strafe Left",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_A,
            actionType = ActionType.TAP_ACTION
        )

        val comboMapping = Mapping(
            id = "map_ctrl_a",
            profileId = "prof_test",
            name = "Select All",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_A,
            secondaryCodes = listOf(KeyEvent.KEYCODE_CTRL_LEFT),
            actionType = ActionType.TAP_ACTION
        )

        resolver.rebuild(listOf(singleKeyMapping, comboMapping))

        // When Ctrl is pressed along with A, combo mapping should win
        val modifierStateWithCtrl = ModifierState(isCtrlPressed = true)
        val resolvedCombo = comboResolver.resolve(
            keyCode = KeyEvent.KEYCODE_A,
            modifierState = modifierStateWithCtrl,
            comboMappings = resolver.getAllComboMappings(),
            keyMappings = resolver.getAllKeyMappings()
        )

        assertNotNull(resolvedCombo)
        assertEquals("Select All", resolvedCombo?.name)

        // When only A is pressed without Ctrl, single key mapping should resolve
        val modifierStateNone = ModifierState()
        val resolvedSingle = comboResolver.resolve(
            keyCode = KeyEvent.KEYCODE_A,
            modifierState = modifierStateNone,
            comboMappings = resolver.getAllComboMappings(),
            keyMappings = resolver.getAllKeyMappings()
        )

        assertNotNull(resolvedSingle)
        assertEquals("Strafe Left", resolvedSingle?.name)
    }

    @Test
    fun testConflictDetection() {
        val existingMapping = Mapping(
            id = "map_fire_1",
            profileId = "prof_test",
            name = "Fire Primary",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_SPACE,
            actionType = ActionType.TAP_ACTION
        )

        val candidateMapping = Mapping(
            id = "map_jump_2",
            profileId = "prof_test",
            name = "Jump Action",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_SPACE,
            actionType = ActionType.TAP_ACTION
        )

        val conflict = resolver.checkConflict(candidateMapping, listOf(existingMapping))
        assertNotNull(conflict)
        assertEquals("Fire Primary", conflict?.existingMapping?.name)
        assertEquals("Jump Action", conflict?.newMapping?.name)
    }
}
