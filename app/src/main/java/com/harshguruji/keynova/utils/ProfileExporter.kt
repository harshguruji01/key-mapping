package com.harshguruji.keynova.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.harshguruji.keynova.data.model.ActionMode
import com.harshguruji.keynova.data.model.ActionType
import com.harshguruji.keynova.data.model.ControlType
import com.harshguruji.keynova.data.model.CursorSettingsDto
import com.harshguruji.keynova.data.model.DeviceType
import com.harshguruji.keynova.data.model.ExportControlDto
import com.harshguruji.keynova.data.model.ExportMappingDto
import com.harshguruji.keynova.data.model.ExportProfileDto
import com.harshguruji.keynova.data.model.InputType
import com.harshguruji.keynova.data.model.Mapping
import com.harshguruji.keynova.data.model.Profile
import com.harshguruji.keynova.data.model.VirtualControl
import java.util.UUID

object ProfileExporter {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportToJson(profile: Profile): String {
        val dto = ExportProfileDto(
            formatVersion = 1,
            profileName = profile.name,
            description = profile.description,
            targetPackage = profile.targetPackage,
            cursorSettings = CursorSettingsDto(
                sensitivityX = profile.cursorSensitivityX,
                sensitivityY = profile.cursorSensitivityY,
                acceleration = profile.cursorAcceleration,
                scrollSensitivity = profile.scrollSensitivity
            ),
            mappings = profile.mappings.map { m ->
                ExportMappingDto(
                    id = m.id,
                    name = m.name,
                    deviceType = m.deviceType.name,
                    inputType = m.inputType.name,
                    inputCode = m.inputCode,
                    secondaryCodes = m.secondaryCodes,
                    actionType = m.actionType.name,
                    actionMode = m.actionMode.name,
                    targetControlId = m.targetControlId,
                    enabled = m.enabled,
                    sensitivity = m.sensitivity
                )
            },
            virtualControls = profile.controls.map { c ->
                ExportControlDto(
                    id = c.id,
                    name = c.name,
                    type = c.type.name,
                    posX = c.posX,
                    posY = c.posY,
                    width = c.width,
                    height = c.height,
                    opacity = c.opacity,
                    radius = c.radius,
                    deadZone = c.deadZone,
                    assignedKeyLabel = c.assignedKeyLabel,
                    styleColorHex = c.styleColorHex,
                    isVisible = c.isVisible
                )
            }
        )
        return gson.toJson(dto)
    }

    fun importFromJson(jsonString: String): Result<Profile> {
        return runCatching {
            val dto = gson.fromJson(jsonString, ExportProfileDto::class.java)
                ?: throw IllegalArgumentException("Invalid profile format")

            val newProfileId = "profile_" + UUID.randomUUID().toString().take(8)

            val mappings = dto.mappings.map { m ->
                Mapping(
                    id = "map_" + UUID.randomUUID().toString().take(8),
                    profileId = newProfileId,
                    name = m.name,
                    deviceType = runCatching { DeviceType.valueOf(m.deviceType) }.getOrDefault(DeviceType.KEYBOARD),
                    inputType = runCatching { InputType.valueOf(m.inputType) }.getOrDefault(InputType.KEYBOARD_KEY),
                    inputCode = m.inputCode,
                    secondaryCodes = m.secondaryCodes,
                    actionType = runCatching { ActionType.valueOf(m.actionType) }.getOrDefault(ActionType.TAP_ACTION),
                    actionMode = runCatching { ActionMode.valueOf(m.actionMode) }.getOrDefault(ActionMode.TAP),
                    targetControlId = m.targetControlId,
                    enabled = m.enabled,
                    sensitivity = m.sensitivity
                )
            }

            val controls = dto.virtualControls.map { c ->
                VirtualControl(
                    id = c.id.ifBlank { "ctrl_" + UUID.randomUUID().toString().take(8) },
                    profileId = newProfileId,
                    name = c.name,
                    type = runCatching { ControlType.valueOf(c.type) }.getOrDefault(ControlType.BUTTON),
                    posX = c.posX,
                    posY = c.posY,
                    width = c.width,
                    height = c.height,
                    opacity = c.opacity,
                    radius = c.radius,
                    deadZone = c.deadZone,
                    assignedKeyLabel = c.assignedKeyLabel,
                    styleColorHex = c.styleColorHex,
                    isVisible = c.isVisible
                )
            }

            Profile(
                id = newProfileId,
                name = dto.profileName.ifBlank { "Imported Profile" },
                description = dto.description,
                targetPackage = dto.targetPackage,
                isDefault = false,
                cursorSensitivityX = dto.cursorSettings.sensitivityX,
                cursorSensitivityY = dto.cursorSettings.sensitivityY,
                cursorAcceleration = dto.cursorSettings.acceleration,
                scrollSensitivity = dto.cursorSettings.scrollSensitivity,
                mappings = mappings,
                controls = controls
            )
        }
    }
}
