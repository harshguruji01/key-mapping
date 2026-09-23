package com.harshguruji.keynova.input

import android.view.KeyEvent
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.KeyCombination
import com.harshguruji.keynova.data.model.Mapping
import java.util.concurrent.ConcurrentHashMap

data class ConflictInfo(
    val inputDescription: String,
    val existingMapping: Mapping,
    val newMapping: Mapping
)

class MappingResolver {

    private val keyToMappingMap = ConcurrentHashMap<Int, Mapping>()
    private val comboToMappingMap = ConcurrentHashMap<KeyCombination, Mapping>()
    private val mouseToMappingMap = ConcurrentHashMap<Int, Mapping>()
    private val controlIdToMappingMap = ConcurrentHashMap<String, MutableList<Mapping>>()

    fun rebuild(mappings: List<Mapping>) {
        keyToMappingMap.clear()
        comboToMappingMap.clear()
        mouseToMappingMap.clear()
        controlIdToMappingMap.clear()

        for (mapping in mappings) {
            if (!mapping.enabled) continue

            when (mapping.inputType) {
                InputType.KEYBOARD_KEY -> {
                    if (mapping.secondaryCodes.isNotEmpty()) {
                        // Multi-key combination mapping
                        var flags = 0
                        for (sec in mapping.secondaryCodes) {
                            when (sec) {
                                KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_CTRL_RIGHT -> flags = flags or KeyEvent.META_CTRL_ON
                                KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_ALT_RIGHT -> flags = flags or KeyEvent.META_ALT_ON
                                KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_SHIFT_RIGHT -> flags = flags or KeyEvent.META_SHIFT_ON
                                KeyEvent.KEYCODE_META_LEFT, KeyEvent.KEYCODE_META_RIGHT -> flags = flags or KeyEvent.META_META_ON
                            }
                        }
                        val combo = KeyCombination(primaryCode = mapping.inputCode, modifierFlags = flags)
                        comboToMappingMap[combo] = mapping
                    } else {
                        keyToMappingMap[mapping.inputCode] = mapping
                    }
                }
                InputType.KEY_COMBINATION -> {
                    val flags = mapping.secondaryCodes.fold(0) { acc, code -> acc or code }
                    val combo = KeyCombination(primaryCode = mapping.inputCode, modifierFlags = flags)
                    comboToMappingMap[combo] = mapping
                }
                InputType.MOUSE_BUTTON -> {
                    mouseToMappingMap[mapping.inputCode] = mapping
                }
                else -> {
                    keyToMappingMap[mapping.inputCode] = mapping
                }
            }

            mapping.targetControlId?.let { ctrlId ->
                controlIdToMappingMap.getOrPut(ctrlId) { mutableListOf() }.add(mapping)
            }
        }
    }

    fun getKeyMapping(keyCode: Int): Mapping? = keyToMappingMap[keyCode]

    fun getComboMapping(combination: KeyCombination): Mapping? = comboToMappingMap[combination]

    fun getMouseMapping(buttonCode: Int): Mapping? = mouseToMappingMap[buttonCode]

    fun getMappingsForControl(controlId: String): List<Mapping> = controlIdToMappingMap[controlId] ?: emptyList()

    fun getAllComboMappings(): Map<KeyCombination, Mapping> = comboToMappingMap

    fun getAllKeyMappings(): Map<Int, Mapping> = keyToMappingMap

    fun checkConflict(candidate: Mapping, existingMappings: List<Mapping>): ConflictInfo? {
        for (existing in existingMappings) {
            if (existing.id == candidate.id || !existing.enabled) continue

            if (existing.inputType == candidate.inputType &&
                existing.inputCode == candidate.inputCode &&
                existing.secondaryCodes == candidate.secondaryCodes
            ) {
                val desc = when (candidate.inputType) {
                    InputType.KEYBOARD_KEY -> runCatching { KeyEvent.keyCodeToString(candidate.inputCode).removePrefix("KEYCODE_") }.getOrDefault("Key ${candidate.inputCode}")
                    InputType.MOUSE_BUTTON -> "Mouse Button ${candidate.inputCode}"
                    else -> "Input ${candidate.inputCode}"
                }
                return ConflictInfo(desc, existing, candidate)
            }
        }
        return null
    }
}
