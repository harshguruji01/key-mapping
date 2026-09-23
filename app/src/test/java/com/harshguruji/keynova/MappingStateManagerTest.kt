package com.harshguruji.keynova

import android.view.KeyEvent
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.MappingState
import com.harshguruji.keynova.input.MappingStateManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MappingStateManagerTest {

    @Test
    fun testTapModeTransitions() = runTest {
        val testScope = TestScope(testScheduler)
        val stateManager = MappingStateManager(testScope)

        val tapMapping = Mapping(
            id = "tap_map_1",
            profileId = "prof_1",
            name = "Test Tap",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_SPACE,
            actionType = ActionType.TAP_ACTION,
            actionMode = ActionMode.TAP
        )

        stateManager.onInputDown(tapMapping)
        assertEquals(MappingState.PRESSED, stateManager.getState(tapMapping.id))

        stateManager.onInputUp(tapMapping)
        assertEquals(MappingState.IDLE, stateManager.getState(tapMapping.id))
    }

    @Test
    fun testHoldModeTransitions() = runTest {
        val testScope = TestScope(testScheduler)
        val stateManager = MappingStateManager(testScope)

        val holdMapping = Mapping(
            id = "hold_map_1",
            profileId = "prof_1",
            name = "Test Hold",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_W,
            actionType = ActionType.HOLD_ACTION,
            actionMode = ActionMode.HOLD,
            holdThresholdMs = 200L
        )

        stateManager.onInputDown(holdMapping)
        assertEquals(MappingState.PRESSED, stateManager.getState(holdMapping.id))

        // Advance coroutine clock past hold threshold
        testScope.advanceTimeBy(250L)
        assertEquals(MappingState.HELD, stateManager.getState(holdMapping.id))

        stateManager.onInputUp(holdMapping)
        assertEquals(MappingState.IDLE, stateManager.getState(holdMapping.id))
    }

    @Test
    fun testToggleModeTransitions() = runTest {
        val testScope = TestScope(testScheduler)
        val stateManager = MappingStateManager(testScope)

        val toggleMapping = Mapping(
            id = "toggle_map_1",
            profileId = "prof_1",
            name = "Test Toggle",
            deviceType = DeviceType.KEYBOARD,
            inputType = InputType.KEYBOARD_KEY,
            inputCode = KeyEvent.KEYCODE_C,
            actionType = ActionType.TOGGLE_ACTION,
            actionMode = ActionMode.TOGGLE
        )

        // First press: ON
        stateManager.onInputDown(toggleMapping)
        assertEquals(MappingState.TOGGLED_ON, stateManager.getState(toggleMapping.id))
        stateManager.onInputUp(toggleMapping)
        assertEquals(MappingState.TOGGLED_ON, stateManager.getState(toggleMapping.id))

        // Second press: OFF
        stateManager.onInputDown(toggleMapping)
        assertEquals(MappingState.TOGGLED_OFF, stateManager.getState(toggleMapping.id))
        stateManager.onInputUp(toggleMapping)
        assertEquals(MappingState.TOGGLED_OFF, stateManager.getState(toggleMapping.id))
    }
}
