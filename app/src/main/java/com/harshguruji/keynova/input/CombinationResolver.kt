package com.harshguruji.keynova.input

import android.view.KeyEvent
import com.harshguruji.keynova.data.model.KeyCombination
import com.harshguruji.keynova.data.model.Mapping

class CombinationResolver {

    /**
     * Resolves an exact or partial combination given a pressed key and the current modifier state.
     * Priority: Exact Combination (Modifiers + Key) > Multi-key mapping > Single key
     */
    fun resolve(
        keyCode: Int,
        modifierState: ModifierState,
        comboMappings: Map<KeyCombination, Mapping>,
        keyMappings: Map<Int, Mapping>
    ): Mapping? {
        val metaFlags = modifierState.toMetaStateFlags()

        // 1. Try exact combination with all active modifiers
        if (modifierState.hasAnyActiveModifier()) {
            val fullCombo = KeyCombination(primaryCode = keyCode, modifierFlags = metaFlags)
            comboMappings[fullCombo]?.let { return it }

            // Check individual modifiers if full combo not matched
            val flagCandidates = listOf(
                KeyEvent.META_CTRL_ON or KeyEvent.META_SHIFT_ON,
                KeyEvent.META_CTRL_ON,
                KeyEvent.META_ALT_ON,
                KeyEvent.META_SHIFT_ON,
                KeyEvent.META_META_ON
            )

            for (flags in flagCandidates) {
                if ((metaFlags and flags) == flags) {
                    val candidate = KeyCombination(primaryCode = keyCode, modifierFlags = flags)
                    comboMappings[candidate]?.let { return it }
                }
            }
        }

        // 2. Fall back to individual key mapping
        return keyMappings[keyCode]
    }
}
